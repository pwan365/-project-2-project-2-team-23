package com.softeng306.p2.DataModel;

public class Tag {
    private String tagName;
    private String tagType;

    public Tag(int newId, String newTagName, String newTagType){
        tagName = newTagName;
        tagType = newTagType;
    }

    public Tag(){}

    public String getTagName(){
        return tagName;
    }

    public String getTagType(){
        return tagType;
    }
}
