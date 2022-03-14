package com.example.qracutie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Player {
    /*
    String playerQRCode;
    String userName;
    String email;
    String phoneNumber;
     */

    String username; // unique alphanumeric identifier
    Bitmap profilePic;; // profile picture of user

    ArrayList<GameQRCode> gameQRCodes = new ArrayList<>(); // all qrCodes belonging to the player
    int highestQRCode = 0; // highest pointed QR code belonging to the player
    int lowestQRCode = Integer.MAX_VALUE; // lowest pointed QR code belonging to the Player
    int pointTotal = 0; // the sum of all QR code points belonging to the Player

    HashMap<String, Bitmap> gameQRCodeImages = new HashMap<>(); // all images belonging to the player, mapped to GameQRCode hashes

    /**
     * public constructor. Creates an instance of the Player class
     * @param username a unique alphanumeric identifier
     */
    public Player(String username) {
        this.username = username;
    }

    /**
     * returns username associated with Player
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * updates username associated with player
     * @param username new username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * returns profile picture associated with player
     * @return Bitmap
     */
    public Bitmap getProfilePic() {
        return profilePic;
    }

    /**
     * updates profile picture associated with player
     * @param profilePic
     */
    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * adds a new QR code to the set of all qr codes belonging to the player. If
     * image parameter is not null, it adds an image to be associated with the QR code.
     * Updates the highest/lowest scoring QR codes and the sum of scores
     * @param qrCode a QR code to belong to the player
     * @param image an image to associate to the QR code, or null
     */
    public void addGameQRCode(GameQRCode qrCode, Bitmap image) {

        int points = qrCode.getPoints();

        // add the qr code to the set of all codes
        gameQRCodes.add(qrCode);

        // add an image, if provided
        if (image != null) {
            gameQRCodeImages.put(qrCode.getHash(), image);
        }

        // add the qr code points to the point total
        pointTotal += points;

        // update minimum and maximum found qr codes
        highestQRCode = Math.max(highestQRCode, points);
        lowestQRCode = Math.min(lowestQRCode, points);
    }

    /**
     * deletes a QR code belonging to the player. Resets the highest scoring
     * QR code, lowest scoring QR code, and point total accordingly
     * @param qrCode a QR code to delete from user collection
     */
    public void deleteGameQRCode(GameQRCode qrCode) {
        // if array list does not contain qr code, throw an error
        if (!gameQRCodes.contains(qrCode)) {
            throw new IllegalArgumentException("QR Code is not owned by player");
        }

        // remove the QR code from the array list
        gameQRCodes.remove(qrCode);

        // update point total
        int points = qrCode.getPoints();
        pointTotal -= points;

        // update highest and lowest scoring qr codes
        if (highestQRCode == points || lowestQRCode == points) {
            highestQRCode = 0;
            lowestQRCode = Integer.MAX_VALUE;
            for (GameQRCode code : gameQRCodes) {
                // update highest and lowest
                highestQRCode = Math.max(highestQRCode, code.getPoints());
                lowestQRCode = Math.min(lowestQRCode, code.getPoints());
            }
        }
    }

    /**
     * returns all GameQRCode objects which belong to the Player's collection.
     * @return GameQRCodes
     */
    public ArrayList<GameQRCode> getGameQRCodes() {
        return gameQRCodes;
    }

    /**
     * returns a hashmap of Bitmap images associated with a game QR code
     * @return hashmap
     */
    public HashMap<String, Bitmap> getGameQRCodeImages() {
        return gameQRCodeImages;
    }

    /**
     * return the QR code belonging to the player which has the highest point value.
     * returns 0 if no codes belong to player
     *
     * @return highest point QR code, or 0 if no codes belong to player
     */
    public int getHighestQRCode() {
        return highestQRCode;
    }

    /**
     * return the QR code belonging to the player which has the lowest point value.
     * returns 0 if no codes belong to player
     *
     * @return highest point QR code, or 0 if no codes belong to player
     */
    public int getLowestQRCode() {
        if (lowestQRCode == Integer.MAX_VALUE) {
            return 0;
        }
        return lowestQRCode;
    }

    /**
     * return the sum of QR code points belonging to the player.
     * returns 0 if no codes belong to player.
     *
     * @return highest point QR code, or 0 if no codes belong to player
     */
    public int getPointTotal() {
        return pointTotal;
    }

    /**
     * returns a count of the total number of GameQRCodes wihtin the collection
     * @return count of GameQRCodes
     */
    public int getTotalCodes() {
        return gameQRCodes.size();
    }
}
