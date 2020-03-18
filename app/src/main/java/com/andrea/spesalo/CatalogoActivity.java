package com.andrea.spesalo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.Model.CatalogoItem;
import com.andrea.spesalo.ViewHolder.CatalogoAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Float.parseFloat;

public class CatalogoActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference catalogo,lista;
    CatalogoAdapter adapter;
    ArrayList<CatalogoItem> listacatalogoCopy = new ArrayList<>();
    ArrayList<CatalogoItem> listacatalogo = new ArrayList<>();
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        database = FirebaseDatabase.getInstance();

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        //Firebase
        database = FirebaseDatabase.getInstance();
        catalogo = database.getReference("Prodotti");

        //RecycleView
        recyclerView = (RecyclerView)findViewById(R.id.listCatalogo);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvlayoutManager = layoutManager;

        recyclerView.setLayoutManager(rvlayoutManager);

        adapter = new CatalogoAdapter(this,listacatalogo);

        adapter.setOnItemClickListener(new CatalogoAdapter.OnItemClickListener(){
            @Override
            public void onAddClick(final int position){
                final CatalogoItem add = listacatalogo.get(position);
                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                lista = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Lista");
                lista.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.child(add.getNome()).exists()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CatalogoActivity.this);
                                builder.setTitle("Attenzione!")
                                        .setMessage("Prodotto già presente nella lista della spesa")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {

                                               }
                                        })
                                        .show();

                            } else {
                                lista.child(add.getNome()).child("Check").setValue("0");
                                if(add.getOfferta()){
                                    lista.child(add.getNome()).child("Offerta").setValue("1");
                                }else{
                                    lista.child(add.getNome()).child("Offerta").setValue("0");
                                }
                                Intent intent = new Intent(CatalogoActivity.this, ListaspesaActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        } catch (Exception exc) {
                            Toast.makeText(CatalogoActivity.this, "Il nome non può contenere simboli diversi da lettere o numeri", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

        });

        final EditText theFilter = (EditText) findViewById(R.id.addelement);

        theFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                listacatalogo.clear();

                if(theFilter.getText().toString().matches("")){
                    for(CatalogoItem x : listacatalogoCopy){
                        CatalogoItem catalogoitem2 = new CatalogoItem();
                        catalogoitem2.setNome(x.getNome());
                        catalogoitem2.setOfferta(x.getOfferta());
                        listacatalogo.add(catalogoitem2);
                    }
                }else{
                    CatalogoItem catalogoitem3 = new CatalogoItem();
                    catalogoitem3.setNome(theFilter.getText().toString());
                    catalogoitem3.setOfferta(false);
                    listacatalogo.add(catalogoitem3);
                    for(CatalogoItem x : listacatalogoCopy){
                        if(x.getNome().toLowerCase().startsWith(theFilter.getText().toString().toLowerCase())){
                            CatalogoItem catalogoitem4 = new CatalogoItem();
                            catalogoitem4.setNome(x.getNome());
                            catalogoitem4.setOfferta(x.getOfferta());
                            listacatalogo.add(catalogoitem4);
                        }

                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CatalogoActivity.AsyncTaskRunner runner = new CatalogoActivity.AsyncTaskRunner();
        runner.execute();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CatalogoActivity.this,
                    "Creazione catalogo in corso",
                    "Attendi..");
        }

        @Override
        protected String doInBackground(String... params) {

            final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            catalogo = database.getReference("Product");
            catalogo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for(DataSnapshot messageSnapshot:children) {
                        CatalogoItem catalogoitem = new CatalogoItem();
                        catalogoitem.setNome(messageSnapshot.child("Nome").getValue().toString());
                        if(messageSnapshot.child("Offerta").getValue().toString().compareTo("1") == 0){
                            catalogoitem.setOfferta(true);
                        }else{
                            catalogoitem.setOfferta(false);
                        }
                        listacatalogo.add(catalogoitem);
                    }
                    listacatalogoCopy = (ArrayList<CatalogoItem>)listacatalogo.clone();
                    recyclerView.setAdapter(adapter);

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

    public void goback(View view){
        super.onBackPressed();
    }




}
