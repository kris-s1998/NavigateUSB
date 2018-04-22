package com.example.kskie.draft3;

/**
 * Created by Taylor on 11/04/2018.
 */


/**
 * A wrapper class containing the URL of the floor image and the level number of the floor
 */
public class Floor {
    private String imageurl;
    private String level;


    public Floor (){

    }

    public Floor (String imageurl, String level){
        this.level = level;
        this.imageurl = imageurl;
    }

    public String getLevel() {
        return level;
    }

    public String getImageurl() {
        return imageurl;
    }
}
