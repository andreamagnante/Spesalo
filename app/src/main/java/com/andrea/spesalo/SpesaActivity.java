package com.andrea.spesalo;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.ViewHolder.SpesaAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpesaActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    TextView giornospesa,totale;

    FirebaseDatabase database;
    DatabaseReference spesa;
    DatabaseReference tot;

    ArrayList<CartItem> cart = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spesa);

        //Firebase
        database = FirebaseDatabase.getInstance();
        spesa = database.getReference("Carrello");

        //RecycleView
        recyclerView = (RecyclerView)findViewById(R.id.listSpesa);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvlayoutManager = layoutManager;

        recyclerView.setLayoutManager(rvlayoutManager);

        final SpesaAdapter adapter = new SpesaAdapter(this,cart);

        String mese = getIntent().getStringExtra("mesescelto3");
        String anno = getIntent().getStringExtra("annoscelto3");
        String giorno = getIntent().getStringExtra("giornoscelto3");
        String id = getIntent().getStringExtra("idscelto3");

        totale = (TextView) findViewById(R.id.totale);
        giornospesa = (TextView) findViewById(R.id.giornospesa);
        giornospesa.setText("Spesa del "+giorno+"/"+mese+"/"+anno+"");

        loadListSpesa(adapter,id,giorno,mese,anno);
    }

    private void loadListSpesa(final SpesaAdapter adapter,final String id,final String giorno,final String mese,final String anno){

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        spesa = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Spesa").child(anno).child(mese).child(id).child(giorno).child("Prodotti");
        spesa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                String nome = "";
                String prezzo = "";
                String quantita = "";
                for(DataSnapshot messageSnapshot:children) {
                    CartItem cartitem = new CartItem();
                    nome = messageSnapshot.getKey();
                    prezzo = (String) messageSnapshot.child("Prezzo").getValue();
                    quantita = (String) messageSnapshot.child("Quantita").getValue();
                    cartitem.setNome(nome);
                    cartitem.setPrezzo(prezzo);
                    cartitem.setQuantita(quantita);

                    cart.add(cartitem);
                }


                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tot = spesa.getParent().child("Totale");
        tot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 totale.setText("TOTALE : "+dataSnapshot.getValue().toString()+" €");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void gotocalendario(View view)
    {
        onBackPressed();
    }
}
