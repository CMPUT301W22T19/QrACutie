package com.example.qracutie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class GameQRCodeAdapter extends ArrayAdapter<GameQRCode> {
    private ArrayList<GameQRCode> qrCodes;
    HashMap<String, Bitmap> qrCodeImages;
    private Context context;

    public GameQRCodeAdapter(Context context, ArrayList<GameQRCode> qrCodes, HashMap<String, Bitmap> qrCodeImages) {
        super(context, 0, qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
        this.qrCodeImages = qrCodeImages;
    }

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
        Bitmap image;
        if (qrCodeImages.containsKey(qrCode.getHash())) {
            image = qrCodeImages.get(qrCode.getHash());
        } else {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_qrcode_image);
        }
        qrCodeImage.setImageBitmap(image);

        // set qr code points
        TextView qrCodePoints = view.findViewById(R.id.qr_code_list_points_val);
        qrCodePoints.setText(String.valueOf(qrCode.getPoints()));

        return view;
    }
}