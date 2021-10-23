package com.softeng306.p2.DataModel;

/**
 * Object to represent a tag used by a vehicle
 */
public class Tag {
    private int id;
    private String tagName;
    private String tagType;

    /**
     * Constructor for Tag
     * @param newId id for the tag
     * @param newTagName the name of the tag
     * @param newTagType the type of the tag
     */
    public Tag(int newId, String newTagName, String newTagType){
        id = newId;
        tagName = newTagName;
        tagType = newTagType;
    }

    /**
     * Constructor for a tag view item
     * @param newTagName
     */
    public Tag(String newTagName){
        tagName = newTagName;
    }

    /**
     * Tmpty constructor for firebase
     */
    public Tag(){}

    /**
     * Getter for the tag's id
     * @return id the id of the tag
     */
    public int getId(){
        return id;
    }

    /**
     * Getter for the tag's name
     * @return tagName the name of the tag
     */
    public String getTagName(){
        return tagName;
    }

    /**
     * Getter for the tag's type
     * @return tagType the type of the tag
     */
    public String getTagType(){
        return tagType;
    }
}
