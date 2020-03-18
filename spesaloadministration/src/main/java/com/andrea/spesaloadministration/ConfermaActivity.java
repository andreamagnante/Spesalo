package com.andrea.spesaloadministration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

import static android.Manifest.permission.CAMERA;
import static java.lang.Float.parseFloat;

public class ConfermaActivity extends AppCompatActivity {

    TextView codiceText,dataText;
    String codice,anno,mese,giorno,id,numerocell,totaleannox,totalemesex,totalcarrellox;
    FirebaseDatabase database;
    DatabaseReference idDB,totannoDB,totmeseDB,carrello,carrello2,totcarrelloDB,totaleAnno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferma);
        database = FirebaseDatabase.getInstance();

        codiceText = (TextView) findViewById(R.id.codice);
        dataText = (TextView) findViewById(R.id.data);

        if(getIntent().getStringExtra("codice") != null){
            codice = getIntent().getStringExtra("codice");
        }

        numerocell = codice.substring(0, codice.indexOf("/"));
        codiceText.setText(numerocell);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        anno = String.valueOf(calendar.get(Calendar.YEAR));
        mese = String.valueOf(calendar.get(Calendar.MONTH)+1);
        giorno = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));


        idDB = database.getReference("User").child(numerocell);
        idDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.child("Id").getValue().toString();
                    dataText.setText(giorno+"/"+mese+"/"+anno);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        totannoDB = database.getReference("User").child(numerocell).child("Spesa").child(anno).child("Totale");
        totannoDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totaleannox = dataSnapshot.getValue().toString();
                }else{
                    totaleannox = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        totcarrelloDB = database.getReference("User").child(numerocell).child("Carrello");
        totcarrelloDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalcarrellox = dataSnapshot.child("Totale").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        totmeseDB = database.getReference("User").child(numerocell).child("Spesa").child(anno).child(mese).child("Totale");
        totmeseDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalemesex = dataSnapshot.getValue().toString();
                }else{
                    totalemesex = "0";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void confermaspesa(View view){
        carrello = database.getReference("User").child(numerocell).child("Carrello");
        carrello2 = database.getReference("User").child(numerocell).child("Spesa").child(anno).child(mese).child(id).child(giorno);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    carrello2.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                setTotali();
                            } else {
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            carrello.addListenerForSingleValueEvent(valueEventListener);
    }

    public void setTotali(){
        database.getReference("User").child(numerocell).child("Carrello").setValue(null);
        database.getReference("User").child(numerocell).child("Id").setValue(String.valueOf(Integer.valueOf(id)+1));
        Float totF = Float.parseFloat(totalcarrellox)+Float.parseFloat(totaleannox);
        String totS = String.valueOf(totF);
        if(totS.substring(totS.indexOf(".") + 1).length() > 2){
            totS = totS.substring(0, totS.indexOf(".")+3);
        }
        database.getReference("User").child(numerocell).child("Spesa").child(anno).child("Totale").setValue(totS);
        Float totF2 = Float.parseFloat(totalcarrellox)+Float.parseFloat(totalemesex);
        String totS2 = String.valueOf(totF2);
        if(totS2.substring(totS2.indexOf(".") + 1).length() > 2){
            totS2 = totS2.substring(0, totS2.indexOf(".")+3);
        }
        database.getReference("User").child(numerocell).child("Spesa").child(anno).child(mese).child("Totale").setValue(totS2);
        Intent intent = new Intent(ConfermaActivity.this, FinalActivity.class);
        ConfermaActivity.this.startActivity(intent);

    }


    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(ConfermaActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}
