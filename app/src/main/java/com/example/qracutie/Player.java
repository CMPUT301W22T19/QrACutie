package com.example.qracutie;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Represents an application user. Allows the user to collect QR codes, track information
 * related to their collection like images, statistics, and geolocations. A Player is
 * identified using their username. Each username is guaranteed to be unique.
 */
public class Player {
    String username = ""; // unique alphanumeric identifier
    String email = "";
    String phoneNumber = "";
    String profileImage = "";

    ArrayList<GameQRCode> gameQRCodes = new ArrayList<>(); // all qrCodes belonging to the player
    int highestQRCode = 0; // highest pointed QR code belonging to the player
    int lowestQRCode = Integer.MAX_VALUE; // lowest pointed QR code belonging to the Player
    int pointTotal = 0; // the sum of all QR code points belonging to the Player
    int totalCodes = 0; // the count of all GameQRCodes collected by the player

    HashMap<String, String> gameQRCodeImages = new HashMap<>(); // all images belonging to the player, mapped to GameQRCode hashes

    /**
     * public constructor. Creates an instance of the Player class
     * @param username a unique alphanumeric identifier
     */
    public Player(String username) {
        this.username = username;
    }

    /**
     * No argument constructor for db
     */
    public Player() {}
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
     * returns email string of the user
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets email of user to specified string
     * @param email new email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * returns the phone number string associated with user
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * sets phone number of user to a specified string
     * @param phoneNumber new phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * returns a URI string that maps to a user profile image in the database
     * @return URI string
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Sets a URI string that maps to a profile image in the database
     * @param profileImage new URI string
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * adds a new QR code to the set of all qr codes belonging to the player. If
     * image parameter is not null, it adds an image to be associated with the QR code.
     * Updates the highest/lowest scoring QR codes and the sum of scores
     * @param qrCode a QR code to belong to the player
     */
    public void addGameQRCode (GameQRCode qrCode){

        int points = qrCode.getPoints();

        // add the qr code to the set of all codes
        gameQRCodes.add(qrCode);


        // add the qr code points to the point total
        pointTotal += points;

        // update minimum and maximum found qr codes
        highestQRCode = Math.max(highestQRCode, points);
        lowestQRCode = Math.min(lowestQRCode, points);

        // update total codes
        totalCodes += 1;
    }

    /**
     * deletes a QR code belonging to the player. Resets the highest scoring
     * QR code, lowest scoring QR code, and point total accordingly
     * @param qrCode a QR code to delete from user collection
     */
    public void deleteGameQRCode (GameQRCode qrCode){
        // if array list does not contain qr code, throw an error
        GameQRCode codeToDelete = null;

        for (int i = 0; i < gameQRCodes.size(); i++) {
            if (gameQRCodes.get(i).getHash().equals(qrCode.getHash())) {
                codeToDelete = gameQRCodes.get(i);
            }
        }

        if (codeToDelete == null) {
            throw new IllegalArgumentException("QR Code is not owned by player");
        }

        // remove the QR code from the array list
        gameQRCodes.remove(codeToDelete);

        // update point total
        int points = codeToDelete.getPoints();
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

        // update total codes
        totalCodes -= 1;
    }

    /**
     * returns all GameQRCode objects which belong to the Player's collection.
     * @return GameQRCodes
     */
    public ArrayList<GameQRCode> getGameQRCodes () {
        return gameQRCodes;
    }

    public Boolean checkIfGameQRCodeScanned(String newCodeHash) {
        for(GameQRCode scannedCodes : getGameQRCodes()){
            if(scannedCodes.getHash().equals(newCodeHash)) return true;
        }
        return false;
    }
    /**
     * returns a hashmap of Bitmap images associated with a game QR code
     * @return hashmap
     */
    public HashMap<String, String> getGameQRCodeImages () {
        return gameQRCodeImages;
    }

    /**
     * return the QR code belonging to the player which has the highest point value.
     * returns 0 if no codes belong to player
     *
     * @return highest point QR code, or 0 if no codes belong to player
     */
    public int getHighestQRCode () {
        return highestQRCode;
    }

    /**
     * return the QR code belonging to the player which has the lowest point value.
     * returns 0 if no codes belong to player
     *
     * @return highest point QR code, or 0 if no codes belong to player
     */
    public int getLowestQRCode () {
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
    public int getPointTotal () {
        return pointTotal;
    }

    /**
     * returns a count of the total number of GameQRCodes wihtin the collection
     * @return count of GameQRCodes
     */
    public int getTotalCodes () {
        return totalCodes;
    }

    public void setGameQRCodes(ArrayList<GameQRCode> newList) {
        this.gameQRCodes = newList;
    }

    public void setGameQRCodeImages(HashMap<String, String> newMap) {
        this.gameQRCodeImages = newMap;
    }

    public void addImage(String hash, String image) {
        this.gameQRCodeImages.put(hash, image);
    }
}
