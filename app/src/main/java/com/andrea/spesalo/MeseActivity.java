package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import java.util.ArrayList;

public class MeseActivity extends AppCompatActivity {

    String annoScelto,meseScelto,meseString;
    TextView datascelta1;

    RecyclerView recyclerView;

    FirebaseDatabase database;
    DatabaseReference spesa;
    DatabaseReference tot;

    ArrayList<CalendarioItem> calendarioList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mese);

        //Firebase
        database = FirebaseDatabase.getInstance();

        //RecycleView
        recyclerView = (RecyclerView)findViewById(R.id.listSpesa);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvlayoutManager = layoutManager;

        recyclerView.setLayoutManager(rvlayoutManager);

        final CalendarioAdapter adapter = new CalendarioAdapter(this,calendarioList);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        meseScelto = getIntent().getStringExtra("mesescelto");
        annoScelto = getIntent().getStringExtra("annoscelto");
        meseString = findMese(Integer.valueOf(meseScelto));
        datascelta1 = (TextView)findViewById(R.id.datascelta);

        datascelta1.setText("Spese di "+meseString+" "+annoScelto);

        adapter.setOnItemClickListener(new CalendarioAdapter.OnItemClickListener(){
            @Override
            public void onSpesaClick(final int position){


                    Intent intent = new Intent(MeseActivity.this, SpesaActivity.class);
                    intent.putExtra("mesescelto3", meseScelto);
                    intent.putExtra("annoscelto3",annoScelto);
                    CalendarioItem mese = calendarioList.get(position);
                    intent.putExtra("idscelto3",mese.getId());
                    intent.putExtra("giornoscelto3",mese.getData().substring(0,mese.getData().indexOf("/")));
                    MeseActivity.this.startActivity(intent);

            }
        });

        loadListMese(adapter);
    }

    private void loadListMese(final CalendarioAdapter adapter){

        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        spesa = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Spesa").child(annoScelto).child(meseScelto);
        spesa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                String id = "";
                String data = "";
                String totale = "";
                int y = 0;

                for(DataSnapshot messageSnapshot:children) {
                    if(messageSnapshot.getKey().compareTo("Totale") != 0) {
                        CalendarioItem calendarioitem = new CalendarioItem();
                        id = messageSnapshot.getKey();
                        for(DataSnapshot messageSnapshot2:messageSnapshot.getChildren()) {
                            data = messageSnapshot2.getKey();
                            totale = (String) messageSnapshot2.child("Totale").getValue().toString();
                        }
                        calendarioitem.setId(id);
                        calendarioitem.setData(""+data+"/"+meseScelto+"/"+annoScelto+"");
                        calendarioitem.setPrezzo(totale);

                        calendarioList.add(calendarioitem);
                    }
                }


                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MeseActivity.this, CalendarioActivity.class);
        intent.putExtra("mesescelto2", meseScelto);
        intent.putExtra("annoscelto2",annoScelto);
        MeseActivity.this.startActivity(intent);
    }

    public void goback(View view){
        Intent intent = new Intent(MeseActivity.this, CalendarioActivity.class);
        intent.putExtra("mesescelto2", meseScelto);
        intent.putExtra("annoscelto2",annoScelto);
        MeseActivity.this.startActivity(intent);
    }


}
