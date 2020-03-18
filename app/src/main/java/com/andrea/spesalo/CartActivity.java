package com.andrea.spesalo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.Model.Product;
import com.andrea.spesalo.ViewHolder.CartAdapter;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import static android.Manifest.permission.CAMERA;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class CartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    CartAdapter adapter;
    int month,year;
    float monthEuro;
    int minimo,massimo;
    FirebaseDatabase database;
    DatabaseReference carrello;
    DatabaseReference table_product;
    DatabaseReference codice_product;
    DatabaseReference listaspesa;
    AlertDialog dialog,dialog2,dialog5;

    TextView totale,nomeElemento,nomeElemento2,nomeElemento3;
    EditText numQuantita;
    ArrayList<CartItem> cart = new ArrayList<>();

    ImageView immagine;
    TextView descrizione,barcode;
    String codice = "";

    ElegantNumberButton nuovaquantita;

    private static final int REQUEST_CAMERA = 1;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        notificationManager = NotificationManagerCompat.from(this);

        if(checkPermission()){

        }
        else{
            requestPermission();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Carrello");
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nomeMenu);
        final String nomekey = getIntent().getStringExtra("nomekey");

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        navUsername.setText(settings.getString("Username", "").toString());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //Firebase
        database = FirebaseDatabase.getInstance();
        carrello = database.getReference("Carrello");

        //RecycleView
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvlayoutManager = layoutManager;

        recyclerView.setLayoutManager(rvlayoutManager);

        adapter = new CartAdapter(this,cart);

        adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener(){
            @Override
            public void onDeleteClick(final int position){

                final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Vuoi veramente eliminare questo prodotto dal carrello?")
                        .setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setNegativeButton("Elimina", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CartItem delete = cart.get(position);
                                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                if(cart.size()==1){
                                    database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").setValue(null);
                                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                                else {
                                    database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").child("Prodotti").child(delete.getNome()).setValue(null);
                                    Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();


            }

            public void onPlusMinusClick(final int position){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CartActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_productadd, null);
                mBuilder.setView(mView);
                CartItem plusminus = cart.get(position);
                nomeElemento = (TextView) mView.findViewById(R.id.nomeElemento);
                nomeElemento.setText(plusminus.getNome());
                dialog = mBuilder.create();
                dialog.show();
                dialog.getWindow().setLayout(600,1200);
            }

            public void onChangeDescriptionClick(final int position){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CartActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_productdescription, null);
                mBuilder.setView(mView);
                final CartItem changedescription = cart.get(position);
                nomeElemento3 = (TextView) mView.findViewById(R.id.nomeElemento3);
                nomeElemento3.setText(changedescription.getNome());
                immagine = (ImageView) mView.findViewById(R.id.product_image5);
                descrizione = (TextView) mView.findViewById(R.id.descrizione);
                barcode = (TextView) mView.findViewById(R.id.barcode);
                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);

                codice_product = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").child("Prodotti").child(changedescription.getNome());
                codice_product.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            barcode.setText(dataSnapshot.child("Codice").getValue().toString());
                            descrizione.setText(dataSnapshot.child("Descrizione").getValue().toString());
                            Picasso.with(getBaseContext())
                                    .load(dataSnapshot.child("Immagine").getValue().toString())
                                    .fit()
                                    .into(immagine);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                dialog5 = mBuilder.create();
                dialog5.show();
            }

        });

        totale = (TextView)findViewById(R.id.total);

        CartActivity.AsyncTaskRunner runner = new CartActivity.AsyncTaskRunner();
        runner.execute();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CartActivity.this,
                    "Creazione carrello",
                    "Attendi..");
        }

        @Override
        protected String doInBackground(String... params) {
            totale = (TextView) findViewById(R.id.total);

            final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").child("Prodotti");
            carrello.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    float numtotale = 0;
                    String nome = "";
                    String prezzo = "";
                    String quantita = "";
                    String prezzofinale = "";
                    for(DataSnapshot messageSnapshot:children) {
                        CartItem cartitem = new CartItem();
                        nome = messageSnapshot.getKey();
                        prezzo = (String) messageSnapshot.child("Prezzo").getValue();
                        quantita = (String) messageSnapshot.child("Quantita").getValue();
                        prezzofinale = (String) messageSnapshot.child("PrezzoFinale").getValue();
                        cartitem.setNome(nome);
                        cartitem.setPrezzo(prezzo);
                        cartitem.setQuantita(quantita);
                        Float prezzoElem = parseFloat(prezzo)*parseFloat(quantita);
                        String totS2 = String.valueOf(prezzoElem);
                        if(totS2.substring(totS2.indexOf(".") + 1).length() > 2){
                            totS2 = totS2.substring(0, totS2.indexOf(".")+3);
                        }
                        cartitem.setPrezzoFinale(totS2);
                        cart.add(cartitem);

                        numtotale = numtotale + (parseFloat(totS2));
                    }

                    String totS = String.valueOf(numtotale);
                    if(totS.substring(totS.indexOf(".") + 1).length() > 2){
                        totS = totS.substring(0, totS.indexOf(".")+3);
                    }
                    recyclerView.setAdapter(adapter);
                    carrello.getParent().child("Totale").setValue(totS);
                    totale.setText(totS);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return "Executed";
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }

    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(CartActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA},REQUEST_CAMERA);
    }


    private void loadListFood(final CartAdapter adapter){


    }


            @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder4 = new AlertDialog.Builder(CartActivity.this);
            builder4.setTitle("Vuoi davvero uscire dall'app?")
                    .setPositiveButton("Rimani", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setNegativeButton("Esci", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    })
                    .show();

        }
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

        } else if (id == R.id.nav_calendar) {
            Intent intent = new Intent(CartActivity.this, CalendarioActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_offerte) {
            Intent intent = new Intent(CartActivity.this, OfferteActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setTitle("Vuoi eseguire davvero il logout?")
                    .setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
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

    public void gotobarcode(View view){
        if(checkPermission()){
            Intent intent = new Intent(CartActivity.this, BarcodeActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(CartActivity.this, "Non puoi accedere allo scanner senza prima aver dato i permessi alla fotocamera", Toast.LENGTH_SHORT).show();

        }
    }

    public void gotolistaspesa(View view){

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        listaspesa = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
        listaspesa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(CartActivity.this, ListaspesaActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CartActivity.this, ListavuotaActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void logoutcode(View view)
    {
        Intent intent = new Intent(CartActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void deletecarrello(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setTitle("Attenzione!")
                .setMessage("Sei sicuro di voler svuotare il carrello?")
                .setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Svuota", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").setValue(null);
                        Intent intent = new Intent(CartActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .show();

    }

    public void gotoacquista(View view)
    {
                Intent intent = new Intent(CartActivity.this, ShakeActivity.class);
                intent.putExtra("totalecarrello",totale.getText().toString());
                startActivity(intent);
    }


    public String meseTostring(int mese){
        String meseString = "";
        if(mese == 1){
            meseString = "gennaio";
        }
        if(mese == 2){
            meseString = "febbraio";
        }
        if(mese == 3){
            meseString = "marzo";
        }
        if(mese == 4){
            meseString = "aprile";
        }
        if(mese == 5){
            meseString = "maggio";
        }
        if(mese == 6){
            meseString = "giugno";
        }
        if(mese == 7){
            meseString = "luglio";
        }
        if(mese == 8){
            meseString = "agosto";
        }
        if(mese == 9){
            meseString = "settembre";
        }
        if(mese == 10){
            meseString = "ottobre";
        }
        if(mese == 11){
            meseString = "novembre";
        }
        if(mese == 12){
            meseString = "dicembre";
        }
        return meseString;
    }

    public void changevalue(View v) {
        String pageNumber = v.getTag().toString();
        database = FirebaseDatabase.getInstance();
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").child("Prodotti").child(nomeElemento.getText().toString()).child("Quantita").setValue(pageNumber);
        Intent intent = new Intent(CartActivity.this, CartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

    public void changevaluemore(View v) {
        dialog.dismiss();
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(CartActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_productaddmore, null);
        nomeElemento2 = (TextView) mView.findViewById(R.id.nomeElemento2);
        nomeElemento2.setText(nomeElemento.getText().toString());
        numQuantita = (EditText) mView.findViewById(R.id.numQuantita);
        database = FirebaseDatabase.getInstance();
        mBuilder.setView(mView);
        mBuilder.setPositiveButton("Confermo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello").child("Prodotti").child(nomeElemento.getText().toString()).child("Quantita").setValue(numQuantita.getText().toString());
                Intent intent = new Intent(CartActivity.this, CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
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

    public void goback5(View v){
        dialog5.dismiss();
    }
}

