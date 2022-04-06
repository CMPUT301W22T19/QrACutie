package com.example.qracutie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The nearby codes adabter allows NearbyQR Codes to be added to an array adapter for
 * display in a listview element within the maps activity.
 */
public class NearByCodesAdapter extends ArrayAdapter<NearbyQRCode> {
    private ArrayList<NearbyQRCode> qrCodes;
    private Context context;

    /**
     * generates a new instance of the GameQRCodeAdapter class
     * @param context the context of the caller class
     * @param qrCodes an array list of GameQRCode objects
     */
    public NearByCodesAdapter(Context context, ArrayList<NearbyQRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
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
            view = LayoutInflater.from(this.context).inflate(R.layout.list_nearby_qr_code, parent, false);
        }

        NearbyQRCode qrCode = qrCodes.get(position);

        // set qr code image
//        ImageView qrCodeImage = view.findViewById(R.id.nearby_qr_code_list_image);
//        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(qrCodeImages.get(qrCode.getHash()));
//        Bitmap image = bitmapdraw.getBitmap();
//
//        qrCodeImage.setImageBitmap(image);


        // set qr code points
        TextView qrCodePoints = view.findViewById(R.id.nearby_qr_code_list_points_val);
        qrCodePoints.setText(String.valueOf(qrCode.getPoints()));

        TextView qrCodeDistance = view.findViewById(R.id.distance);
        double distance = Double.valueOf(qrCode.getDistance());
        DecimalFormat df = new DecimalFormat("###.##");
        qrCodeDistance.setText(df.format(distance) + " m");

        ImageView qrCodeImage = view.findViewById(R.id.nearby_qr_code_list_image);
        qrCodeImage.setImageBitmap(qrCode.getImage());

        return view;
    }
}
