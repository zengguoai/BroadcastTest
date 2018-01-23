package com.example.localreceivertest;

/**
 * Created by weiguanghua on 18-1-23.
 */

public class Contacts {
    private String name;
    private String number;

    public Contacts(String name,String number){
        this.name = name;
        this.number = number;
    }
    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
