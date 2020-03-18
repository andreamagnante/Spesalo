package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView zXingScannerView;
    FirebaseDatabase database;
    DatabaseReference carrello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

        database = FirebaseDatabase.getInstance();

    }


    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    public void onResume() {
        super.onResume();
        if(zXingScannerView == null) {
                    zXingScannerView = new ZXingScannerView(this);
                    setContentView(zXingScannerView);
        }
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    DatabaseReference table_product;

    @Override
    public void handleResult(Result result) {
        final String barCode = result.getText();

        table_product = database.getReference("Product");

        if(!barCode.isEmpty()){
            getDetailProduct(barCode);
        }

    }

    private void getDetailProduct(final String barCode){

            table_product.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.child(barCode).exists()) {

                            Intent intent = new Intent(BarcodeActivity.this, ProductaddActivity.class);
                            intent.putExtra("codice", barCode);

                            BarcodeActivity.this.startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this);
                            builder.setTitle("Attenzione")
                                    .setMessage("Prodotto inesistente o errore nella scannerizzazione!")
                                    .setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                            carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
                                            carrello.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Intent intent = new Intent(BarcodeActivity.this, CartActivity.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("Riprova", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            zXingScannerView.resumeCameraPreview(BarcodeActivity.this);
                                        }
                                    })
                                    .show();

                        }
                    }catch (Exception exc) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this);
                        builder.setTitle("Attenzione")
                                .setMessage("Codice non conforme con l'applicazione!")
                                .setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        onBackPressed();
                                    }
                                })
                                .setNegativeButton("Riprova", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        zXingScannerView.resumeCameraPreview(BarcodeActivity.this);
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

}
