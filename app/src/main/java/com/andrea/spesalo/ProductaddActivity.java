package com.andrea.spesalo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;


import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.Model.Product;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class ProductaddActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference carrello;
    AlertDialog dialog,dialog2;
    TextView nomeElemento,nomeElemento2;
    String url = "";



    public void onBackPressed(){
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(ProductaddActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ProductaddActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    String codice="";
    TextView nome,descrizione,prezzo,prezzofinale;
    ImageView immagine;
    Button quantita;

    DatabaseReference table_product;
    DatabaseReference table_cart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productadd);

        database = FirebaseDatabase.getInstance();
        table_product =database.getReference("Product");

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PRESSHOLDKEY_LITE,300);
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 400 milliseconds
        v.vibrate(400);


        nome = (TextView) findViewById(R.id.product_name);
        descrizione = (TextView) findViewById(R.id.product_description);
        prezzo = (TextView) findViewById(R.id.cart_item_price);
        immagine = (ImageView) findViewById(R.id.product_image);
        quantita = (Button) findViewById(R.id.cart_item_plusminus);
        prezzofinale = (TextView) findViewById(R.id.cart_item_price_final);

        if(getIntent() != null){
            codice = getIntent().getStringExtra("codice");
        }

        table_product.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.child(codice).getValue(Product.class);

                url = product.getImmagine();
                prezzo.setText(product.getPrezzo());
                nome.setText(product.getNome());
                descrizione.setText(product.getDescrizione());
                prezzofinale.setText(product.getPrezzo());
                quantita.setText("1");
                Picasso.with(getBaseContext()).load(product.getImmagine())
                        .fit()
                        .into(immagine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void gotoCarrello(View view)
    {
        nome = (TextView) findViewById(R.id.product_name);
        prezzo = (TextView) findViewById(R.id.cart_item_price);
        quantita = (Button) findViewById(R.id.cart_item_plusminus);
        prezzofinale = (TextView) findViewById(R.id.cart_item_price_final);

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        table_cart = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").child("Prodotti");

        table_cart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(nome.getText().toString()).exists()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductaddActivity.this);
                    builder.setTitle("Attenzione! Prodotto già presente nel carrello.")
                            .setMessage("Vuoi aggiungere le quantità selezionate a quelle già presenti?")
                            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ProductaddActivity.this, CartActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int numtot = parseInt(quantita.getText().toString()) + parseInt(dataSnapshot.child(nome.getText().toString()).child("Quantita").getValue().toString());
                                    String quantitaTot = String.valueOf(numtot);
                                    table_cart.child(nome.getText().toString()).child("Quantita").setValue(quantitaTot);
                                    table_cart.child(nome.getText().toString()).child("Prezzo").setValue(prezzo.getText().toString());
                                    Intent intent = new Intent(ProductaddActivity.this, CartActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();

                }
                else{
                    table_cart.child(nome.getText().toString()).child("Quantita").setValue(quantita.getText().toString());
                    table_cart.child(nome.getText().toString()).child("Prezzo").setValue(prezzo.getText().toString());
                    table_cart.child(nome.getText().toString()).child("Codice").setValue(codice);
                    table_cart.child(nome.getText().toString()).child("Immagine").setValue(url);
                    table_cart.child(nome.getText().toString()).child("Descrizione").setValue(descrizione.getText().toString());
                    Intent intent = new Intent(ProductaddActivity.this, CartActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void goback(View view){
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(ProductaddActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ProductaddActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void plusminus(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProductaddActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_productadd, null);
        mBuilder.setView(mView);
        nomeElemento = (TextView) mView.findViewById(R.id.nomeElemento);
        nomeElemento.setText(nome.getText().toString());
        dialog = mBuilder.create();
        dialog.show();
        dialog.getWindow().setLayout(600,1200);
    }

    public void changevalue(View v) {
        String pageNumber = v.getTag().toString();
        quantita.setText(pageNumber);
        prezzofinale.setText(String.format("%.2f",parseFloat(prezzo.getText().toString())*parseInt(pageNumber)));
        dialog.dismiss();
    }

    public void changevaluemore(View v) {
        dialog.dismiss();
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProductaddActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_productaddmore, null);
        nomeElemento2 = (TextView) mView.findViewById(R.id.nomeElemento2);
        nomeElemento2.setText(nome.getText().toString());
        final EditText numQuantita = (EditText) mView.findViewById(R.id.numQuantita);
        mBuilder.setView(mView);
        mBuilder.setPositiveButton("Confermo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                quantita.setText(numQuantita.getText().toString());
                prezzofinale.setText(String.format("%.2f",parseFloat(prezzo.getText().toString())*parseInt(numQuantita.getText().toString())));
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dialog2 = mBuilder.create();
        dialog2.show();


    }


}
