package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrea.spesalo.Model.ListaItem;
import com.andrea.spesalo.Model.OffertaItem;
import com.andrea.spesalo.ViewHolder.ListaAdapter;
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

public class ListaspesaActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference carrello;
    DatabaseReference lista,listaofferte2;
    RelativeLayout offertaLayout2;
    ListaAdapter adapter;

    RecyclerView recyclerView;

    ArrayList<ListaItem> listaspesa = new ArrayList<>();
    ListaItem checkElement;
    private static ViewPager mPager2;
    private static int currentPage2 = 0;
    private static int NUM_PAGES2 = 0;

    ArrayList<OffertaItem> offertaList2 = new ArrayList<>();
    DataSnapshot offerteLista2;
    EditText elemento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaspesa);

        database = FirebaseDatabase.getInstance();

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        //Firebase
        database = FirebaseDatabase.getInstance();
        lista = database.getReference("Lista");

        //RecycleView
        recyclerView = (RecyclerView)findViewById(R.id.listLista);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvlayoutManager = layoutManager;

        recyclerView.setLayoutManager(rvlayoutManager);

        adapter = new ListaAdapter(this,listaspesa);

        adapter.setOnItemClickListener(new ListaAdapter.OnItemClickListener(){
            @Override
            public void onDeleteClick(final int position){

                final AlertDialog.Builder builder = new AlertDialog.Builder(ListaspesaActivity.this);
                builder.setTitle("Vuoi veramente eliminare questo elemento dalla lista?")
                        .setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("Elimina", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ListaItem delete = listaspesa.get(position);
                                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                if(listaspesa.size()==1){
                                    database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista").setValue(null);
                                    Intent intent = new Intent(ListaspesaActivity.this, ListavuotaActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                                else {
                                    database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista").child(delete.getNome()).setValue(null);
                                    Intent intent = new Intent(ListaspesaActivity.this, ListaspesaActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();


            }
            @Override
            public void onCheckClick(final int position){
                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                checkElement = listaspesa.get(position);
                lista = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
                lista.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        try{
                            Integer xElement = Integer.valueOf(dataSnapshot.child(checkElement.getNome()).child("Check").getValue().toString());
                            if(xElement == 0){
                                lista.child(checkElement.getNome()).child("Check").setValue("1");
                            }else{
                                lista.child(checkElement.getNome()).child("Check").setValue("0");
                            }

                        }catch (Exception exc) {
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }



        });

        offertaLayout2 = (RelativeLayout)findViewById(R.id.offertaList2);

        listaofferte2 = database.getReference("Offerte");
        listaofferte2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    offerteLista2 = dataSnapshot;
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for(DataSnapshot messageSnapshot:children) {
                        OffertaItem offertaitem2 = new OffertaItem();
                        offertaitem2.setNome(messageSnapshot.child("Nome").getValue().toString());
                        offertaitem2.setOriginale(messageSnapshot.child("Originale").getValue().toString());
                        offertaitem2.setOfferta(messageSnapshot.child("Offerta").getValue().toString());

                        offertaList2.add(offertaitem2);
                    }
                    init2();
                }else{
                    offertaLayout2.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadListElementi(adapter);
    }

    private void init2() {

        mPager2 = (ViewPager) findViewById(R.id.pager2);
        mPager2.setAdapter(new SlidingOfferte_Adapter(ListaspesaActivity.this,offertaList2,1));

        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator2);

        indicator.setViewPager(mPager2);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES2 = Math.toIntExact(offerteLista2.getChildrenCount());

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage2 == NUM_PAGES2) {
                    currentPage2 = 0;
                }
                mPager2.setCurrentItem(currentPage2++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 5000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage2 = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }


    private void loadListElementi(final ListaAdapter adapter){

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        lista = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
        lista.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                String nome = "";
                Boolean checklista = false;
                Boolean offerta = false;
                for(DataSnapshot messageSnapshot:children) {
                    ListaItem listaitem = new ListaItem();
                    nome = messageSnapshot.getKey();
                    Integer valore = Integer.valueOf(messageSnapshot.child("Check").getValue().toString());
                    if(valore == 0){
                        checklista = false;
                    }else{
                        checklista = true;
                    }
                    Integer valore2 = Integer.valueOf(messageSnapshot.child("Offerta").getValue().toString());
                    if(valore2 == 0){
                        offerta = false;
                    }else{
                        offerta = true;
                    }

                    listaitem.setNome(nome);
                    listaitem.setCheckboxLista(checklista);
                    listaitem.setOfferta(offerta);
                    listaspesa.add(listaitem);
                }


                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        Intent intent = new Intent(ListaspesaActivity.this, CartActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ListaspesaActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }



    public void deletelista(View view)
    {
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        lista = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
        lista.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListaspesaActivity.this);
                    builder.setTitle("Attenzione!")
                            .setMessage("Sei sicuro di voler svuotare la lista?")
                            .setPositiveButton("Mantieni", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setNegativeButton("Svuota", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                    database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista").setValue(null);
                                    Intent intent = new Intent(ListaspesaActivity.this, ListavuotaActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListaspesaActivity.this);
                    builder.setTitle("Impossibile procedere")
                            .setMessage("Lista già vuota!")
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void addlista(View view)
    {
        Intent intent = new Intent(ListaspesaActivity.this, CatalogoActivity.class);
        startActivity(intent);
    }



    public void goback(View view){
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(ListaspesaActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ListaspesaActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inserisciInLista2(int positionList){
        final String elementoscelto = offertaList2.get(positionList).getNome();
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
                            if (dataSnapshot.child(elementoscelto).exists()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ListaspesaActivity.this);
                                builder.setTitle("Attenzione!")
                                        .setMessage("Prodotto già presente nella lista della spesa")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();

                            } else {
                                lista.child(elementoscelto).child("Check").setValue("0");
                                lista.child(elementoscelto).child("Offerta").setValue("1");
                                Intent intent = new Intent(ListaspesaActivity.this, ListaspesaActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        } catch (Exception exc) {
                            Toast.makeText(ListaspesaActivity.this, "Il nome non può contenere simboli diversi da lettere o numeri", Toast.LENGTH_SHORT).show();
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
