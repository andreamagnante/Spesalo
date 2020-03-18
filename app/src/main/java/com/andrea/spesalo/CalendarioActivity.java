package com.andrea.spesalo;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.andrea.spesalo.Model.CalendarioItem;
import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.ViewHolder.CalendarioAdapter;
import com.andrea.spesalo.ViewHolder.SpesaAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference calendariodb;
    DatabaseReference carrello;
    String intentx,k,w,meseNum;
    String annoScelto2,meseScelto2;
    int x = 0, meseVar;
    TextView annocalendario,soldianno,mesecalendario,soldimese;
    ImageButton destracalendario,sinistracalendario;
    DataSnapshot annoLista;
    int annoCal,meseCal;

    Bundle extras = new Bundle();

    RecyclerView recyclerView;
    ArrayList<CalendarioItem> calendarioList = new ArrayList<>();

    private static final String TAG = "CalendarActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Le mie spese");
        setSupportActionBar(toolbar);

        //Firebase
        database = FirebaseDatabase.getInstance();
        carrello = database.getReference("Carrello");

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

        annocalendario = (TextView) findViewById(R.id.annoCalendario);
        soldianno = (TextView) findViewById(R.id.soldianno);
        mesecalendario = (TextView) findViewById(R.id.meseCalendario);
        soldimese = (TextView) findViewById(R.id.soldimese);
        sinistracalendario = (ImageButton) findViewById(R.id.sinistracalendario);
        destracalendario = (ImageButton) findViewById(R.id.destracalendario);

        if(getIntent().getStringExtra("annoscelto2") != null){
            meseScelto2 = getIntent().getStringExtra("mesescelto2");
            annoScelto2 = getIntent().getStringExtra("annoscelto2");
            caricaSoldi(annoScelto2,meseScelto2);
        }else {
            caricaSoldi("","");
        }

    }

    public void caricaSoldi(String anno,String mese){
        if(anno == "" && mese == ""){
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            annoCal = calendar.get(Calendar.YEAR);
            meseCal = calendar.get(Calendar.MONTH)+1;
        }else{
            annoCal = Integer.parseInt(anno);
            meseCal = Integer.parseInt(mese);
        }
        annocalendario.setText(String.valueOf(annoCal));
        String mesefind = findMese(meseCal);
        mesecalendario.setText(mesefind);
        meseVar = meseCal;

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        calendariodb = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Spesa");
        calendariodb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                annoLista = dataSnapshot;
                if (dataSnapshot.child(String.valueOf(annoCal)).exists()) {
                    soldianno.setText(dataSnapshot.child(String.valueOf(annoCal)).child("Totale").getValue().toString());
                    if(dataSnapshot.child(String.valueOf(annoCal)).child(String.valueOf(meseCal)).exists()){
                        soldimese.setText(dataSnapshot.child(String.valueOf(annoCal)).child(String.valueOf(meseCal)).child("Totale").getValue().toString());
                    }else{
                        soldimese.setText("0");
                    }
                }else{
                    soldianno.setText("0");
                    soldimese.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void caricaSoldi2(String anno){
        k = anno;
        annocalendario.setText(anno);
        if (annoLista.child(k).exists()) {
            soldianno.setText(annoLista.child(k).child("Totale").getValue().toString());
            if(annoLista.child(k).child(String.valueOf(meseVar)).exists()){
                soldimese.setText(annoLista.child(k).child(String.valueOf(meseVar)).child("Totale").getValue().toString());
            }else{
                soldimese.setText("0");
            }
        }else{
            soldianno.setText("0");
            soldimese.setText("0");
        }
    }

    public void sinistraAnno(View view){
        String anno = annocalendario.getText().toString();
        Integer x = Integer.parseInt(anno);
        if(x == 2017){
            AlertDialog.Builder builder = new AlertDialog.Builder(CalendarioActivity.this);
            builder.setTitle("Questa app è nata nel 2017!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .show();
        }else {
            annoCal = x-1;
            meseCal = 1;
            caricaSoldi2(String.valueOf(annoCal));
            caricaSoldi3(meseCal);
        }

    }

    public void destraAnno(View view){
        String anno = annocalendario.getText().toString();
        Integer x = Integer.parseInt(anno);
        if(x == 2019){
            AlertDialog.Builder builder = new AlertDialog.Builder(CalendarioActivity.this);
            builder.setTitle("Ci dispiace, purtroppo non siamo in grado di prevedere il futuro!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .show();
        }else {
            annoCal = x+1;
            meseCal = 1;
            caricaSoldi2(String.valueOf(annoCal));
            caricaSoldi3(1);
        }
    }

    public void caricaSoldi3(Integer mese){
        String mesefind = findMese(mese);
        mesecalendario.setText(mesefind);
        meseVar = mese;
        String k = annocalendario.getText().toString();
        if (annoLista.child(k).exists()) {
            soldianno.setText(annoLista.child(k).child("Totale").getValue().toString());
            if(annoLista.child(k).child(String.valueOf(meseVar)).exists()){
                soldimese.setText(annoLista.child(k).child(String.valueOf(meseVar)).child("Totale").getValue().toString());
            }else{
                soldimese.setText("0");
            }
        }else{
            soldianno.setText("0");
            soldimese.setText("0");
        }
    }

    public void destraMese(View view){
        Integer x = meseVar;
        if(x == 12){
            x = 1;
        }else {
            x = x + 1;
        }
        meseCal = x;
        caricaSoldi3(meseCal);

    }

    public void sinistraMese(View view){
        Integer x = meseVar;
        if(x == 1){
            x = 12;
        }else {
            x = x - 1;
        }
        meseCal = x;
        caricaSoldi3(meseCal);

    }

    public String findMese(int mese){
        String meseString = "";
        if(mese == 1){
            meseString = "Gennaio";
        }
        else if(mese == 2){
            meseString = "Febbraio";
        }
        else if(mese == 3){
            meseString = "Marzo";
        }
        else if(mese == 4){
            meseString = "Aprile";
        }
        else if(mese == 5){
            meseString = "Maggio";
        }
        else if(mese == 6){
            meseString = "Giugno";
        }
        else if(mese == 7){
            meseString = "Luglio";
        }
        else if(mese == 8){
            meseString = "Agosto";
        }
        else if(mese == 9){
            meseString = "Settembre";
        }
        else if(mese == 10){
            meseString = "Ottobre";
        }
        else if(mese == 11){
            meseString = "Novembre";
        }
        else if(mese == 12){
            meseString = "Dicembre";
        }
        return meseString;
    }

    public void scopri(View view){
        String soldi = soldimese.getText().toString();
        if(soldi == "0") {
            AlertDialog.Builder builder = new AlertDialog.Builder(CalendarioActivity.this);
            builder.setTitle("Nessuna spesa effettuata in questo mese!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .show();
        }else {
            Intent intent = new Intent(CalendarioActivity.this, MeseActivity.class);
            intent.putExtra("mesescelto", String.valueOf(meseCal));
            intent.putExtra("annoscelto",annocalendario.getText().toString());
            CalendarioActivity.this.startActivity(intent);
        }
    }


    @Override
    public void onBackPressed() {
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(CalendarioActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CalendarioActivity.this, MainActivity.class);
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
                        Intent intent = new Intent(CalendarioActivity.this, CartActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CalendarioActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (id == R.id.nav_calendar) {


        } else if (id == R.id.nav_offerte) {
            Intent intent = new Intent(CalendarioActivity.this, OfferteActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CalendarioActivity.this);
            builder.setTitle("Vuoi eseguire davvero il logout?")
                    .setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(CalendarioActivity.this, LoginActivity.class);
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
