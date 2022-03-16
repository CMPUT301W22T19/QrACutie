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

    /**
     * Constructor for the object
     * @param commentString
     * @param uID
     * @param date
     */
    Comment(String commentString, String uID, String date){
        this.commentString = commentString;
        this.uID = uID;
        this.date = date;
    }

    /**
     * Returns the string storing the comment text
     * @return
     */
    String getCommentName(){
        return this.commentString;
    }

    /**
     * Returns the uid of teh user who made the comment
     * @return
     */
    String getUserName(){
        return this.uID;
    }

    /**
     * Returns the date the comment was posted on
     * @return
     */
    String getDate(){
        return this.date;
    }
}
