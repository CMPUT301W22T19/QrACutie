package com.example.qracutie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An array adapter which is used to display a list of QR codes within the
 * PlayerCollectionActivity. FOr each QR code, displays an image and an amount of points
 */
public class GameQRCodeAdapter extends ArrayAdapter<GameQRCode> {
    private ArrayList<GameQRCode> qrCodes;
    HashMap<String, String> qrCodeImages;
    private Context context;

    boolean showOptions;

    /**
     * generates a new instance of the GameQRCodeAdapter class
     * @param context the context of the caller class
     * @param qrCodes an array list of GameQRCode objects
     * @param qrCodeImages a hashmap of QRCode identifiers and images saved as Bitmaps
     */
    public GameQRCodeAdapter(Context context, ArrayList<GameQRCode> qrCodes, HashMap<String, String> qrCodeImages, boolean showOptions) {
        super(context, 0, qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
        this.qrCodeImages = qrCodeImages;
        this.showOptions = showOptions;
    }

    /**
     * generates and returns a View element representing a single GameQRCode, which is part
     * of a ListView
     * @param position the index of the view within a list
     * @param convertView an old view to reuse
     * @param parent the list which contains the view
     * @return View element
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_game_qr_code, parent, false);
        }

        GameQRCode qrCode = qrCodes.get(position);

        // set qr code image
        ImageView qrCodeImage = view.findViewById(R.id.qr_code_list_image);
        Glide.with(context).clear(qrCodeImage);
        if (qrCodeImages.containsKey(qrCode.getHash())) {
            Glide.with(context).asBitmap().load(Uri.parse(qrCodeImages.get(qrCode.getHash()))).into(qrCodeImage);
        } else {
            qrCodeImage.setImageResource(R.drawable.default_qrcode_image);
        }

        // set qr code points
        TextView qrCodePoints = view.findViewById(R.id.qr_code_list_points_val);
        qrCodePoints.setText(String.valueOf(qrCode.getPoints()));

        // set qr code scans
        TextView qrCodeScans = view.findViewById(R.id.qr_code_list_scans_val);
        qrCodeScans.setText(String.valueOf(qrCode.getAmountOfScans()));

        // add click listener on entire QR code
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlayerCollectionActivity) context).viewCommentsActivity(qrCode);
            }
        });

        Button optionsButton = view.findViewById(R.id.options_button);
        if (showOptions) {
            // add click listener on more options button
            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PlayerCollectionActivity) context).showOptions(qrCode);
                }
            });
        } else {
            // make options button invisible
            optionsButton.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}