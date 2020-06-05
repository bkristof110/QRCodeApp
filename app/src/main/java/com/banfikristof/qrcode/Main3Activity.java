package com.banfikristof.qrcode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main3Activity extends AppCompatActivity {

    EditText etBemenet;
    Button general, share;
    ImageView qrKod;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        init();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action != null && action.equals(Intent.ACTION_SEND)) {
            if (type.equals("text/plain")) {
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(intent.getStringExtra(Intent.EXTRA_TEXT), BarcodeFormat.QR_CODE, 1000, 1000);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    bitmap = encoder.createBitmap(bitMatrix);
                    qrKod.setImageBitmap(bitmap);
                    share.setEnabled(true);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }

        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBemenet.getText().toString().isEmpty()){
                    Toast.makeText(Main3Activity.this,"Please write something.",Toast.LENGTH_SHORT).show();
                    return;
                }
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(etBemenet.getText().toString(), BarcodeFormat.QR_CODE,1000,1000);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    bitmap = encoder.createBitmap(bitMatrix);
                    qrKod.setImageBitmap(bitmap);
                    share.setEnabled(true);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File path = new File(getCacheDir(), "qrcodes");
                    path.mkdirs();
                    FileOutputStream stream = new FileOutputStream(path + "/qr.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    Uri contentUri = FileProvider.getUriForFile(Main3Activity.this, "com.banfikristof.qrcode.fileprovider", new File(path, "qr.png"));
                    if (contentUri != null) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(Intent.createChooser(shareIntent, "Share"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void init() {
        etBemenet = findViewById(R.id.etBemenet);
        general = findViewById(R.id.btnGenerate);
        qrKod = findViewById(R.id.ivQRkod);
        share = findViewById(R.id.btnShare);
    }
}
