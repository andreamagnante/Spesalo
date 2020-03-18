package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrea.spesalo.Model.OffertaItem;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ListavuotaActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference listaofferte,carrello,lista;
    RelativeLayout offertaLayout;
    TextView nomeoff,origPrezzo,offPrezzo;
    DataSnapshot offerteLista;
    int iterator = 0;
    ArrayList<OffertaItem> offertaList = new ArrayList<>();

    EditText elemento;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listavuota);
        offertaLayout = (RelativeLayout)findViewById(R.id.offertaList1);
        database = FirebaseDatabase.getInstance();
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        listaofferte = database.getReference("Offerte");
        listaofferte.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    offerteLista = dataSnapshot;
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot messageSnapshot:children) {
                        OffertaItem offertaitem = new OffertaItem();
                        offertaitem.setNome(messageSnapshot.child("Nome").getValue().toString());
                        offertaitem.setOriginale(messageSnapshot.child("Originale").getValue().toString());
                        offertaitem.setOfferta(messageSnapshot.child("Offerta").getValue().toString());

                        offertaList.add(offertaitem);
                    }
                    init();
                }else{
                    offertaLayout.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingOfferte_Adapter(ListavuotaActivity.this,offertaList,0));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

       //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = Math.toIntExact(offerteLista.getChildrenCount());

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(ListavuotaActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ListavuotaActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createlista(View view){
        /*AlertDialog.Builder mBuilder = new AlertDialog.Builder(ListavuotaActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_lista, null);

        mBuilder.setView(mView);

        elemento = (EditText) mView.findViewById(R.id.elemento);

        mBuilder.setPositiveButton("Confermo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (elemento.getText().toString().length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListavuotaActivity.this);
                    builder.setTitle("Attenzione!")
                            .setMessage("Impossibile aggiungere elemento vuoto")
                            .show();
                } else if (elemento.getText().toString().length() > 12) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListavuotaActivity.this);
                    builder.setTitle("Nome elemento troppo lungo")
                            .setMessage("Impossibile aggiungere elemento alla lista")
                            .show();
                } else {
                    final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                    lista = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
                    lista.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.child(elemento.getText().toString()).exists()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ListavuotaActivity.this);
                                    builder.setTitle("Attenzione!")
                                            .setMessage("Prodotto già presente nella lista")
                                            .show();

                                } else {
                                    lista.child(elemento.getText().toString()).child("Check").setValue("0");
                                    lista.child(elemento.getText().toString()).child("Offerta").setValue("0");
                                    Intent intent = new Intent(ListavuotaActivity.this, ListaspesaActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            } catch (Exception exc) {
                                Toast.makeText(ListavuotaActivity.this, "Il nome non può contenere simboli diversi da lettere o numeri", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        mBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
        */
        Intent intent = new Intent(ListavuotaActivity.this, CatalogoActivity.class);
        startActivity(intent);
    }

    public void gotocart(View view){
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(ListavuotaActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ListavuotaActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inserisciInLista(int positionList){
        final String elementoscelto = offertaList.get(positionList).getNome();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Hai selezionato il prodotto in offerta "+elementoscelto);
        builder.setMessage("Vuoi inserirlo all'interno della lista della spesa?");
        builder.setPositiveButton("Inserisci elemento", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                lista = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
                lista.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        try {
                            lista.child(elementoscelto).child("Check").setValue("0");
                            lista.child(elementoscelto).child("Offerta").setValue("1");
                            Intent intent = new Intent(ListavuotaActivity.this, ListaspesaActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);

                        } catch (Exception exc) {
                            Toast.makeText(ListavuotaActivity.this, "Il nome non può contenere simboli diversi da lettere o numeri", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

}
