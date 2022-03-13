package com.example.qracutie;

public class GameQRCode {
    String hash;
    int points;

    int longitude;
    int latitude;
    int amountOfScans; // ask for most unique method

    String[] CommentSection; //temp

    /**
     * return the point value associated with the QR code
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * returns the has associated with the QR code
     * @return hash
     */
    public String getHash() {
        return hash;
    }
}
