package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrea.spesalo.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity {


    public void gotologin(View view)
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    EditText etSmartphone,etUsername,etPassword;
    Button bRegister;
    int x = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.registerUser);
        etPassword = (EditText) findViewById(R.id.registerPassword);
        etSmartphone = (EditText) findViewById(R.id.registerSmartphone);

        bRegister = (Button) findViewById(R.id.registerButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        bRegister.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder builder;
            @Override
            public void onClick(View v) {
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (etUsername.equals("") || etSmartphone.equals("") || etPassword.equals("") || etPassword.length() < 8) {
                            builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Attenzione!");
                            builder.setMessage("Riempire tutti i campi oppure verificare che la password sia lunga almeno 8 caratteri.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            //Check if already exist
                            if (dataSnapshot.child(etSmartphone.getText().toString()).exists() && x == 1) {
                                Toast.makeText(RegisterActivity.this, "Utente già esistente!", Toast.LENGTH_SHORT).show();
                            }
                            else if(x == 1){
                                x = 0;
                                User user = new User(etUsername.getText().toString(), etPassword.getText().toString());
                                table_user.child(etSmartphone.getText().toString()).child("Info").setValue(user);
                                table_user.child(etSmartphone.getText().toString()).child("Notifiche").setValue("Accetto");
                                Toast.makeText(RegisterActivity.this, "Utente registrato con successo!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
