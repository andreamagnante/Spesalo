package com.andrea.spesaloadministration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FinalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
    }

    public void tornahome(View view){

            Intent intent = new Intent(FinalActivity.this, MainActivity.class);
            FinalActivity.this.startActivity(intent);

    }
}
