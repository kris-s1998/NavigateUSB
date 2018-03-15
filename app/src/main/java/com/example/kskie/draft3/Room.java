package com.example.kskie.draft3;

/**
 * Created by kskie on 25/02/2018.
 */

public class Room {
    String firstName;
    String lastName;
    String group;
    String level;
    String number;
    String viaRoom;

    public Room(){

    }

    public Room(String firstName, String group, String lastName, String level, String number, String viaRoom) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.level = level;
        this.number = number;
        this.viaRoom = viaRoom;
    }
}
