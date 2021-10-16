package com.softeng306.p2.DataModel;

public class Tag {
    private int id;
    private String tagName;
    private String tagType;

    public Tag(int newId, String newTagName, String newTagType){
        id = newId;
        tagName = newTagName;
        tagType = newTagType;
    }

    public Tag(){}

    public int getId(){
        return id;
    }

    public String getTagName(){
        return tagName;
    }

    public String getTagType(){
        return tagType;
    }
}
