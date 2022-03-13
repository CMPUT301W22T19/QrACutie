package com.example.qracutie;

public class Comment {
    private String commentString;
    private String uID;
    private String date;

    Comment(String commentString, String uID, String date){
        this.commentString = commentString;
        this.uID = uID;
        this.date = date;
    }

    String getCityName(){
        return this.commentString;
    }

    String getProvinceName(){
        return this.uID;
    }

    String getDate(){
        return this.date;
    }
}
