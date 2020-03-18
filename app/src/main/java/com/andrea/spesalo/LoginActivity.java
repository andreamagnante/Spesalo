package com.andrea.spesalo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.andrea.spesalo.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public void gotosign(View view)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    EditText etSmartphone,etPassword;
    Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etSmartphone = (EditText) findViewById(R.id.loginSmartphone);
        etPassword = (EditText) findViewById(R.id.loginPassword);

        bLogin = (Button) findViewById(R.id.loginButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        bLogin.setOnClickListener(new View.OnClickListener() {

            AlertDialog.Builder builder;
            @Override
            public void onClick(View view) {
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Get User information
                        if (etSmartphone.equals("") || etPassword.equals("") || etPassword.length() < 8) {
                            builder = new AlertDialog.Builder(LoginActivity.this);
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
                            if (dataSnapshot.child(etSmartphone.getText().toString()).exists()) {
                                User user = dataSnapshot.child(etSmartphone.getText().toString()).child("Info").getValue(User.class);
                                if (user.getPassword().equals(etPassword.getText().toString())) {
                                    Toast.makeText(LoginActivity.this, "Login avvenuto con successo!", Toast.LENGTH_SHORT).show();


                                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("Smartphone",etSmartphone.getText().toString());
                                    editor.putString("Username",user.getUsername());
                                    editor.commit();

                                    if(dataSnapshot.child(etSmartphone.getText().toString()).child("Carrello").exists()) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setTitle("Attenzione! Carrello ancora pieno dei prodotti precedentemente scannerizzati.")
                                                .setMessage("Cosa preferisci fare?")
                                                .setPositiveButton("Mantieni", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent intent = new Intent(LoginActivity.this, CartActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("Svuota", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        table_user.child(etSmartphone.getText().toString()).child("Carrello").setValue(null);
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .show();

                                    }else{
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login fallito!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Utente non esistente", Toast.LENGTH_SHORT).show();
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

