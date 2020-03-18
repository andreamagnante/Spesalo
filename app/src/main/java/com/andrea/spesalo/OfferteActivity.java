package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrea.spesalo.Model.OffertaItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OfferteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference carrello;
    DatabaseReference offerta;

    ArrayList<OffertaItem> listaofferte = new ArrayList<>();
    TextView nome2,originale2,off2,nome4,originale4,off4,nome6,originale6,off6,nome8,originale8,off8,nome10,originale10,off10;
    RelativeLayout layout1,layout2,layout3,layout4,layout5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerte);

        database = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Offerte");
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nomeMenu);
        String nomekey = getIntent().getStringExtra("nomekey");

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        navUsername.setText(settings.getString("Username", "").toString());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        nome2 = (TextView) findViewById(R.id.nome2);
        originale2 = (TextView) findViewById(R.id.originale2);
        off2 = (TextView) findViewById(R.id.off2);
        nome4 = (TextView) findViewById(R.id.nome4);
        originale4 = (TextView) findViewById(R.id.originale4);
        off4 = (TextView) findViewById(R.id.off4);
        nome6 = (TextView) findViewById(R.id.nome6);
        originale6 = (TextView) findViewById(R.id.originale6);
        off6 = (TextView) findViewById(R.id.off6);
        nome8 = (TextView) findViewById(R.id.nome8);
        originale8 = (TextView) findViewById(R.id.originale8);
        off8 = (TextView) findViewById(R.id.off8);
        nome10 = (TextView) findViewById(R.id.nome10);
        originale10 = (TextView) findViewById(R.id.originale10);
        off10 = (TextView) findViewById(R.id.off10);

        layout1 = (RelativeLayout) findViewById(R.id.offerta2);
        layout2 = (RelativeLayout) findViewById(R.id.offerte3_4);
        layout3 = (RelativeLayout) findViewById(R.id.offerte5_6);
        layout4 = (RelativeLayout) findViewById(R.id.offerte7_8);
        layout5 = (RelativeLayout) findViewById(R.id.offerte9_10);

        attivaofferte();
    }

    public void attivaofferte(){
        offerta = database.getReference("Offerte");
        offerta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                String nome = "";
                String originale = "";
                String offerta = "";
                for (DataSnapshot messageSnapshot : children) {
                    OffertaItem offertaitem = new OffertaItem();
                    nome = (String) messageSnapshot.child("Nome").getValue();
                    originale = (String) messageSnapshot.child("Originale").getValue();
                    offerta = (String) messageSnapshot.child("Offerta").getValue();

                    offertaitem.setNome(nome);
                    offertaitem.setOriginale(originale);
                    offertaitem.setOfferta(offerta);

                    listaofferte.add(offertaitem);
                }

                if(listaofferte.size()==0){
                    layout1.setVisibility(View.INVISIBLE);
                    layout2.setVisibility(View.INVISIBLE);
                    layout3.setVisibility(View.INVISIBLE);
                    layout4.setVisibility(View.INVISIBLE);
                    layout5.setVisibility(View.INVISIBLE);
                }
                else {
                    switch (listaofferte.size()) {
                        case 1:
                            nome2.setText(listaofferte.get(0).getNome());
                            originale2.setText(listaofferte.get(0).getOriginale());
                            off2.setText(listaofferte.get(0).getOfferta());
                            layout2.setVisibility(View.INVISIBLE);
                            layout3.setVisibility(View.INVISIBLE);
                            layout4.setVisibility(View.INVISIBLE);
                            layout5.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            nome2.setText(listaofferte.get(0).getNome());
                            originale2.setText(listaofferte.get(0).getOriginale());
                            off2.setText(listaofferte.get(0).getOfferta());
                            nome4.setText(listaofferte.get(1).getNome());
                            originale4.setText(listaofferte.get(1).getOriginale());
                            off4.setText(listaofferte.get(1).getOfferta());
                            layout3.setVisibility(View.INVISIBLE);
                            layout4.setVisibility(View.INVISIBLE);
                            layout5.setVisibility(View.INVISIBLE);
                            break;
                        case 3:
                            nome2.setText(listaofferte.get(0).getNome());
                            originale2.setText(listaofferte.get(0).getOriginale());
                            off2.setText(listaofferte.get(0).getOfferta());
                            nome4.setText(listaofferte.get(1).getNome());
                            originale4.setText(listaofferte.get(1).getOriginale());
                            off4.setText(listaofferte.get(1).getOfferta());
                            nome6.setText(listaofferte.get(2).getNome());
                            originale6.setText(listaofferte.get(2).getOriginale());
                            off6.setText(listaofferte.get(2).getOfferta());
                            layout4.setVisibility(View.INVISIBLE);
                            layout5.setVisibility(View.INVISIBLE);
                            break;
                        case 4:
                            nome2.setText(listaofferte.get(0).getNome());
                            originale2.setText(listaofferte.get(0).getOriginale());
                            off2.setText(listaofferte.get(0).getOfferta());
                            nome4.setText(listaofferte.get(1).getNome());
                            originale4.setText(listaofferte.get(1).getOriginale());
                            off4.setText(listaofferte.get(1).getOfferta());
                            nome6.setText(listaofferte.get(2).getNome());
                            originale6.setText(listaofferte.get(2).getOriginale());
                            off6.setText(listaofferte.get(2).getOfferta());
                            nome8.setText(listaofferte.get(3).getNome());
                            originale8.setText(listaofferte.get(3).getOriginale());
                            off8.setText(listaofferte.get(3).getOfferta());
                            layout5.setVisibility(View.INVISIBLE);
                            break;
                        case 5:
                            nome2.setText(listaofferte.get(0).getNome());
                            originale2.setText(listaofferte.get(0).getOriginale());
                            off2.setText(listaofferte.get(0).getOfferta());
                            nome4.setText(listaofferte.get(1).getNome());
                            originale4.setText(listaofferte.get(1).getOriginale());
                            off4.setText(listaofferte.get(1).getOfferta());
                            nome6.setText(listaofferte.get(2).getNome());
                            originale6.setText(listaofferte.get(2).getOriginale());
                            off6.setText(listaofferte.get(2).getOfferta());
                            nome8.setText(listaofferte.get(3).getNome());
                            originale8.setText(listaofferte.get(3).getOriginale());
                            off8.setText(listaofferte.get(3).getOfferta());
                            nome10.setText(listaofferte.get(4).getNome());
                            originale10.setText(listaofferte.get(4).getOriginale());
                            off10.setText(listaofferte.get(4).getOfferta());
                            break;
                    }
                }
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
                    Intent intent = new Intent(OfferteActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(OfferteActivity.this, MainActivity.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
            carrello.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Intent intent = new Intent(OfferteActivity.this, CartActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(OfferteActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (id == R.id.nav_calendar) {
            Intent intent = new Intent(OfferteActivity.this, CalendarioActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_offerte) {

        }
        else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OfferteActivity.this);
            builder.setTitle("Vuoi eseguire davvero il logout?")
                    .setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(OfferteActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
