package com.example.qracutie;

/**
 * Models a single comment on a saved QR Code. Stores the comment text, the user who posted it,
 * and the date when it was posted
 *
 * Adapted in large parts from the MainActivity Class shown in CMPUT 301's labs
 */
public class Comment {

    private String commentString;
    private String uID;
    private String date;

    Comment(String commentString, String uID, String date){
        this.commentString = commentString;
        this.uID = uID;
        this.date = date;
    }

    String getCommentName(){
        return this.commentString;
    }

    String getUserName(){
        return this.uID;
    }

    String getDate(){
        return this.date;
    }
}
