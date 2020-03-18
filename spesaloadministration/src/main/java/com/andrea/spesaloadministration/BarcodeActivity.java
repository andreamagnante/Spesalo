package com.andrea.spesaloadministration;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView zXingScannerView;

    String activity;
    FirebaseDatabase database;
    DatabaseReference table_product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
            zXingScannerView = new ZXingScannerView(getApplicationContext());
            setContentView(zXingScannerView);
            zXingScannerView.setResultHandler(this);
            zXingScannerView.startCamera();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SharedPreferences settings = getSharedPreferences("ActivityInfo", 0);
        activity = settings.getString("activity", "").toString();

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



    @Override
    public void handleResult(Result result) {
        final String barCode = result.getText();

        database = FirebaseDatabase.getInstance();
        table_product = database.getReference("Product");

        if(!barCode.isEmpty()){
            getDetailProduct(barCode);
        }

    }

    private void getDetailProduct(final String barCode){

            table_product.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Intent intent = new Intent(BarcodeActivity.this, ConfermaActivity.class);
                        intent.putExtra("codice", barCode);
                        BarcodeActivity.this.startActivity(intent);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

}
