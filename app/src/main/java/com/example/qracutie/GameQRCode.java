package com.example.qracutie;

import com.google.type.LatLng;

/**
 * This class represents the Game QR Codes that players can scan to get points
 */
public class GameQRCode {
    private String hash;
    private int points;
    private double latitude;
    private double longitude;
    private int amountOfScans; // ask for most unique method

    private String[] CommentSection; //temp

    /**
     * Constructor for the object if the player chooses to only record the points and hash
     * @param hash
     * @param points
     */
    public GameQRCode(String hash, int points) {
        this.hash = hash;
        this.points = points;
        this.amountOfScans = 1;
    }

    /**
     * Constructor for the object if the player chooses to record the location along with the points
     * @param hash
     * @param points
     * @param latitude
     * @param longitude
     */
    public GameQRCode(String hash, int points, double latitude, double longitude) {
        this.hash = hash;
        this.points = points;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amountOfScans = 1;
    }

    /**
     * No arg Constructor to create class from data fetched from Database
     */
    public GameQRCode() {};

    /**
     * Returns the hash of the QR Code
     * @return
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns the amount of points of the QR Code
     * @return
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns the amount times the QR code has been scanned by all players
     * @return
     */
    public int getAmountOfScans() {
        return amountOfScans;
    }

    /**
     * Sets the amount times the QR code has been scanned by all players
     * @return
     */
    public void setAmountOfScans(int count) { this.amountOfScans = count; }

    /**
     * Increments the amount of scans of the QR Code by 1
     */
    public void incrementAmountOfScans() { this.amountOfScans++; }

    /**
     * Returns the list of comments for this QR Code
     * @return
     */
    public String[] getCommentSection() {
        return CommentSection;
    }

    /**
     * Returns the LatLng object which contains the Latitude and Longitude of where the
     * QR Code was scanned
     * @return
     */
    /**
     * Returns the Latitude of the QR Code
     * @return Latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude of the QR Code
     * @return Longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the Latitude of the QR Code
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the longitude of the QR Code
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
