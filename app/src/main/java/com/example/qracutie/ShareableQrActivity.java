package com.example.qracutie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * Class to model a Shareable QR Code Display Page
 *
 * When opened, the class generates a qr code that can be shared with other app user. The two types
 * of qr codees are login: and information: and are passed with the intent when the activity
 * is opened
 *
 * Adapted in large parts from https://www.geeksforgeeks.org/how-to-generate-qr-code-in-android/
 */
public class ShareableQrActivity extends AppCompatActivity {

    public static final String EXTRA_COMMENTS_TYPE = "com.example.qracutie.EXTRA_COMMENTS_TYPE";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "username";

    private String username = "";
    private String qrType = "";
    private ImageView qrCodeIV;

    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shareable_qr);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(TEXT, "");

        // get intent from the class that called it
        Intent intent = getIntent();
        qrType = intent.getStringExtra(EXTRA_COMMENTS_TYPE);

        // create encoding string
        String toEncode = qrType + ":" + username;

        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);

        // below line is for getting the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(toEncode, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();

            // the bitmap is set inside our image view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        }
        catch (WriterException e) {

            // this method is called for exception handling.
            Log.e("Tag", e.toString());
        }
    }
}
