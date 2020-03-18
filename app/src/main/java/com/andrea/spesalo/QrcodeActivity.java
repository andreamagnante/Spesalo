package com.andrea.spesalo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrea.spesalo.Model.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static java.lang.Float.parseFloat;

public class QrcodeActivity extends AppCompatActivity {

    public final static int QRcodeWidth = 500 ;
    private static final String IMAGE_DIRECTORY = "/QRcodeDemonuts";
    Bitmap bitmap ;
    private String etqr;
    private ImageView iv,iv2,immagines;
    Button tornahome,indietro;
    LinearLayout linearQR;
    FirebaseDatabase database;
    DatabaseReference carrello;
    String totalecarrelloString2;
    int y = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        totalecarrelloString2 = getIntent().getStringExtra("totalecarrello2");
        //Firebase
        database = FirebaseDatabase.getInstance();

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 400 milliseconds
        v.vibrate(400);

        iv = (ImageView) findViewById(R.id.iv);
        iv2 = (ImageView) findViewById(R.id.iv2);
        immagines = (ImageView) findViewById(R.id.immagines);
        linearQR = (LinearLayout) findViewById(R.id.linearQR);
        tornahome = (Button)findViewById(R.id.tornahome);
        indietro = (Button)findViewById(R.id.indietro);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();


            final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
            carrello.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(y!=0){
                       immagines.setImageResource(R.drawable.spesaeffettuata);
                       iv.setVisibility(View.INVISIBLE);
                       iv2.setVisibility(View.VISIBLE);
                       linearQR.setBackgroundColor(Color.parseColor("#66ff00"));
                       tornahome.setVisibility(View.VISIBLE);
                       indietro.setVisibility(View.INVISIBLE);
                    }else{
                        y = 1;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.nero):getResources().getColor(R.color.giallo);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(QrcodeActivity.this,
                    "Creazione QR-CODE in corso",
                    "Attendi..");
        }

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            etqr = settings.getString("Smartphone", "")+"/"+totalecarrelloString2;

            if(etqr.trim().length() == 0){
                Toast.makeText(QrcodeActivity.this, "Enter String!", Toast.LENGTH_SHORT).show();
            }else {
                try {
                    bitmap = TextToImageEncode(etqr);

                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }

            return "Executed";
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            iv.setImageBitmap(bitmap);
        }

    }

    public void goindietro(View view){
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        carrello = database.getReference("User").child(settings.getString("Smartphone", "").toString()).child("Carrello");
        carrello.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(QrcodeActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(QrcodeActivity.this, MainActivity.class);
                    startActivity(intent);
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
                    Intent intent = new Intent(QrcodeActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(QrcodeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}