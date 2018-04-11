package com.example.kskie.draft3;

/**
 * Created by kskie on 25/02/2018.
 */

public class Room {
    private String firstName;
    private String lastName;
    private String group;
    private String level;
    private String number;
    private String viaRoom;
    private String image; //image url in firebase database

    public Room(){

    }

    public Room(String firstName, String group, String lastName, String level, String number, String viaRoom, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.level = level;
        this.number = number;
        this.viaRoom = viaRoom;
        this.image = image;
    }

    public Room(Room toBeCloned){
        firstName = toBeCloned.getFirstName();
        lastName = toBeCloned.getLastName();
        number = toBeCloned.getNumber();
        group = toBeCloned.getGroup();
        level = toBeCloned.getLevel();
        viaRoom = toBeCloned.getViaRoom();
        image = toBeCloned.getImage();

    }

    public String getFirstName() {
        return firstName;
    }

    public String getImage() { return image;}

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getViaRoom() {
        return viaRoom;
    }

    public void setViaRoom(String viaRoom) {
        this.viaRoom = viaRoom;
    }



}
