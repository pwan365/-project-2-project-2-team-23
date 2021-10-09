package com.softeng306.p2.Model;

public class TagModel {
    String tName;
    String tType;

    public TagModel(String tName, String tType){
        this.tName = tName;
        this.tType = tType;
    };

    public String getTName(){
        return tName;
    }

    public String getTType(){
        return tType;
    }
}
