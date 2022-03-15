package com.example.qracutie;

import static android.content.ContentValues.TAG;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qracutie.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Retrieves player location and shows Qr Codes within a certain range
 *
 * Code for accessing location, asking for permissions, and onLocationChange listener have been used from -
 * https://github.com/mohsinulkabir14/An-Android-Application-to-Show-Your-Position-On-The-Map-Using-Google-Maps-API/blob/master/app/src/main/java/com/example/maplocation/MapsActivity.java
 *
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int REQUEST_LOCATION_PERMISSION =1 ;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker playerMarker;
    private ActivityMapsBinding binding;
    Circle circle;
    HashMap<String, GameQRCode> qrCodes = new HashMap<>();
    HashMap<String, MarkerOptions> markersOnMap = new HashMap<>();
    Button backButton;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(view -> {
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        });

        /* Everything underneath this section is referenced from
        https://github.com/mohsinulkabir14/An-Android-Application-to-Show-Your-Position-On-The-Map-Using-Google-Maps-API/blob/master/app/src/main/java/com/example/maplocation/MapsActivity.java
        */

        /* Location Manager use either the Network provider or GPS provider of the android device to fetch the location
        latitude or longitude of the user */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // This part asks for location access permission from the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        locationListener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationChanged(Location location) {
                // User's latitude and longitude is fetched here using the location object
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                double difference = getDistance(latitude, longitude, playerMarker.getPosition().latitude, playerMarker.getPosition().longitude);

                // To avoid constant flicker from redrawing markers and circle, we only want to draw if the user has moved far enough
                if(difference > 10) {
                    mMap.clear();
                    if (playerMarker != null) {
                        playerMarker.remove();
                    }
                    // The latitude and longitude is combined and placed on the google map using a marker in the following part
                    LatLng latLng = new LatLng(latitude, longitude);
                    playerMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    mMap.setMinZoomPreference(10.0f);

                    if (circle != null) {
                        circle.remove();
                    }
                    circle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(250)
                            .strokeColor(Color.BLACK));
                    updateQRCodeMarkers();
                }
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
    }
    private void initializeListQRCodes() {

        db.collection("GameQRCodes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GameQRCode gameQRCode = document.toObject(GameQRCode.class);
                                qrCodes.put(gameQRCode.getHash(), gameQRCode);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        for(Map.Entry<String, GameQRCode> entry : qrCodes.entrySet()) {
            GameQRCode qrCode = entry.getValue();
            addBarcodeMarker(qrCode);
        }
    }

    private void updateQRCodes(){
        db.collection("GameQRCodes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    GameQRCode addedQRCode = dc.getDocument().toObject(GameQRCode.class);
                                    qrCodes.put(addedQRCode.getHash(), addedQRCode);
                                    addBarcodeMarker(addedQRCode);
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    GameQRCode modifiedQRCode = dc.getDocument().toObject(GameQRCode.class);
                                    qrCodes.put(modifiedQRCode.getHash(), modifiedQRCode);
                                    MarkerOptions modifiedMarker = markersOnMap.remove(modifiedQRCode.getHash());
                                    modifiedMarker.visible(false);
                                    addBarcodeMarker(modifiedQRCode);
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    GameQRCode removedQRCode = dc.getDocument().toObject(GameQRCode.class);
                                    qrCodes.remove(removedQRCode.getHash());
                                    MarkerOptions removedMarker = markersOnMap.remove(removedQRCode.getHash());
                                    removedMarker.visible(false);
                                    break;
                            }
                        }

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateQRCodeMarkers() {
        for(Map.Entry<String, MarkerOptions> entry : markersOnMap.entrySet()) {
            MarkerOptions marker = entry.getValue();
            String hash = entry.getKey();
            double distance = getDistance(marker.getPosition().latitude, marker.getPosition().longitude, playerMarker.getPosition().latitude, playerMarker.getPosition().longitude);
            if (distance > 250) {
                marker.visible(false);
            }
            else{
                marker.visible(true);
            }
            markersOnMap.put(hash, marker);
            mMap.addMarker(marker);
        }
    }

    /**
     * This function uses the latitude and longitude from 2 locations to determine their distance
     * and returns that distance as a double
     *
     * The following function was obtained from the answer of this stackoverflow question
     * https://stackoverflow.com/questions/49839437/how-to-show-markers-only-inside-of-radius-circle-on-maps
     * @param LAT1 Location 1 Latitude
     * @param LONG1 Location 1 Longitude
     * @param LAT2 Location 2 Latitude
     * @param LONG2 Location 2 Longitude
     * @return
     */
    public double getDistance(double LAT1, double LONG1, double LAT2, double LONG2) {
        double distance = 2 * 6371000 * Math.asin(Math.sqrt(Math.pow((Math.sin((LAT2 * (3.14159 / 180) - LAT1 * (3.14159 / 180)) / 2)), 2) + Math.cos(LAT2 * (3.14159 / 180)) * Math.cos(LAT1 * (3.14159 / 180)) * Math.sin(Math.pow(((LONG2 * (3.14159 / 180) - LONG1 * (3.14159 / 180)) / 2), 2))));
        return distance;
    }

    private void addBarcodeMarker(GameQRCode qrCodes) {
        LatLng qrLocation = new LatLng(qrCodes.getLatitude(), qrCodes.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(qrLocation).title("Barcode: " + qrCodes.getPoints());
        double distance = getDistance(marker.getPosition().latitude, marker.getPosition().longitude, playerMarker.getPosition().latitude, playerMarker.getPosition().longitude);
        if (distance > 250) {
            marker.visible(false);
        }
        else{
            marker.visible(true);
        }
        int height = 100;
        int width = 100;
        int barcode_icon = R.drawable.stock_barcode_icon;

        if (qrCodes.getPoints() < 500) barcode_icon = R.drawable.green_barcode_icon;
        if (qrCodes.getPoints() >= 500 && qrCodes.getPoints() < 1750) barcode_icon = R.drawable.yellow_barcode_icon;
        if (qrCodes.getPoints() >= 1750 && qrCodes.getPoints() < 3000) barcode_icon = R.drawable.orange_barcode_icon;
        if (qrCodes.getPoints() >= 3000 && qrCodes.getPoints() < 6500) barcode_icon = R.drawable.red_barcode_icon;
        if (qrCodes.getPoints() >= 6500) barcode_icon = R.drawable.purple_barcode_icon;

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(barcode_icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        // adding marker
        mMap.addMarker(marker);
        markersOnMap.put(qrCodes.getHash(), marker);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a temp marker near the UofA.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        playerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(53.526002, -113.525522)).title("You"));
        initializeListQRCodes();
        updateQRCodes();
    }
}