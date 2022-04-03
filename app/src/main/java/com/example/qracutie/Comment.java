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
     * generates a new instance of the Comment class, having a comment string, user ID,
     * and a date
     * @param commentString the comment
     * @param uID the ID of the user who created the comment
     * @param date the date when the comment was made
     */
    Comment(String commentString, String uID, String date){
        this.commentString = commentString;
        this.uID = uID;
        this.date = date;
    }

    /**
     * returns the commented text itself
     * @return commented text
     */
    String getCommentName(){
        return this.commentString;
    }

    /**
     * returns the ID of the user who made the comment
     * @return user ID
     */
    String getUserName(){
        return this.uID;
    }

    /**
     * returns the date when the comment was created
     * @return date of comment
     */
    String getDate(){
        return this.date;
    }
}
