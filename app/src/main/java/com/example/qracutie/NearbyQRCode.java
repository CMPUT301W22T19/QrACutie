package com.example.qracutie;

import android.graphics.Bitmap;

/**
 * The nearby QR code represents a QR code on the map which can be seen within the user's
 * searchable radius. Unlike the gamQRCode, the nearby QR code has a relative distance
 * rather than a longitude and latitude
 */
public class NearbyQRCode {
    private int points;
    private String hash;
    private String distance;
    private Bitmap image;


    public NearbyQRCode(int points, String hash, String distance, Bitmap image) {
        this.points = points;
        this.hash = hash;
        this.distance = distance;
        this.image = image;
    }

    public int getPoints() {
        return points;
    }

    public String getDistance() {
        return distance;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getHash() {
        return hash;
    }
}
