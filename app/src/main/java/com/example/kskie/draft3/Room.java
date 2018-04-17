package com.example.kskie.draft3;

/**This class is used to store information about rooms loaded from the database. The rooms are pulled from the database and
 * stored as objects of this class for manipulation within the application.
 *
 * Created by Kris Skierniewski on 25/02/2018.
 */

public class Room {
    private String firstName; //the first name of a staff member in this room
    private String lastName; //the last name of a staff member in this room
    private String group; //the group which the staff member in this room is associated with
    private String level; //the level which the room is on
    private String number; //the room number
    private String viaRoom; //via Room is the room has one
    private String image; //image url in Firebase database

    public Room(){
        //empty constructor required for converting Firebase objects into java objects
    }

    //constructor which initialises all the fields of the room
    public Room(String firstName, String group, String lastName, String level, String number, String viaRoom, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.level = level;
        this.number = number;
        this.viaRoom = viaRoom;
        this.image = image;
    }

    //getter methods for all the fields
    public String getFirstName() {
        return firstName;
    }

    public String getImage() { return image;}

    public String getLastName() {
        return lastName;
    }

    public String getGroup() {
        return group;
    }

    public String getLevel() {
        return level;
    }

    public String getNumber() {
        return number;
    }

    public String getViaRoom() {
        return viaRoom;
    }




}
