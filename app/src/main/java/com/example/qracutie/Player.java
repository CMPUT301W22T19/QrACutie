package com.example.qracutie;

import java.util.HashMap;

public class Player {
    /*
    private String playerQRCode;
    private String userName;
    private String email;
    private String phoneNumber;
    private int pointTotal;
    private String highestPointQRCode;
    private GameQRCode[] scannedCodes;
    private HashMap<String, Images> imagesHashMap = new HashMap<String, Images>();
     */

    private String username;

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
