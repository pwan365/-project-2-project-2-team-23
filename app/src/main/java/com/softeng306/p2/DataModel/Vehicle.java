package com.softeng306.p2.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class used to represent a vehicle, needs to be extended by other classes
 */
public class Vehicle {
    private final int IMAGE_COUNT = 3;

    private int id;
    private String vehicleName;
    private Map<String, String> tags = new HashMap<>();
    private float price;
    private String dimension;
    private float weight;
    private String description;
    private int manufacturedDate;

    /**
     * Constructor of a vehicle
     * @param id
     * @param name
     */
    public Vehicle(int id, String name){
        this.id = id;
        vehicleName = name;
    }

    /**
     * Constructor to display vehicle preview as a item listing
     */
    public Vehicle(String vName, Float vPrice){
        this.vehicleName = vName;
        this.price = vPrice;
    }

    /**
     * Empty constructor for firebase
     */
    public Vehicle(){}

    /**
     * Getter for the id of the vehicle
     * @return id
     */
    public int getId(){
        return id;
    }

    /**
     * Getter for the name of the vehicle
     * @return vehicle name
     */
    public String getVehicleName(){
        return vehicleName;
    }

    /**
     * Add a tag to this vehicle
     * @param newTag tag to add to the vehicle
     */
    public void addTag(Tag newTag){
        String tagName = newTag.getTagName();
        String tagType = newTag.getTagType();
        tags.put(tagName, tagType);
    }

    /**
     * Getter for all the tags
     * @return a map of tags
     */
    public Map<String, String> getTags(){
        return tags;
    }

    /**
     * Check if a vehicle has a certain tag
     * @param tag the tag to check
     * @return if the vehicle has a tag
     */
    public boolean hasTag(Tag tag){
        if (tags.get(tag.getTagName()) == null){
            return false;
        }
        return true;
    }

    /**
     * Check if a vehicle has a certain tag
     * @param tagName the tag to check
     * @return if the vehicle has a tag
     */
    public boolean hasTag(String tagName){
        if (tags.get(tagName) == null){
            return false;
        }
        return true;
    }

    /**
     * Getter for the price of the vehicle
     * @return price
     */
    public float getPrice(){
        return price;
    }

    public void setPrice(float newPrice){
        price = newPrice;
    }

    /**
     * Getter for the Dimension of the vehicle
     * @return Dimension
     */
    public String getDimension() {
        return dimension;
    }

    public void setDimension(String newDimension){
        dimension = newDimension;
    }

    /**
     * Getter for the Weight of the vehicle
     * @return Weight
     */
    public float getWeight(){
        return weight;
    }

    public void setWeight(float newWeight){
        weight = newWeight;
    }

    /**
     * Getter for the Description of the vehicle
     * @return Description
     */
    public String getDescription(){
        return description;
    }

    public void setDescription(String newDescription){
        description = newDescription;
    }

    /**
     * Getter for the Manufactured Date of the vehicle
     * @return Manufactured Date
     */
    public int getManufacturedDate(){
        return manufacturedDate;
    }

    public void setManufacturedDate(int i){
        manufacturedDate = i;
    }

    /**
     * Check if the name of a vehicle contains a string
     * @param str input string
     * @return if the name of a vehicle contains the input string
     */
    public boolean containsString(String str){
        return vehicleName.replaceAll("\\s", "").toLowerCase().contains(str.replaceAll("\\s", "").toLowerCase());
    }

    /**
     * Get the image names for this vehicle
     * @return a list of image names
     */
    public List<String> getImageNames(){
        List<String> names = new ArrayList<>();
        String imageName = vehicleName.replaceAll("\\s", "_").toLowerCase();
        for (int i = 0; i < IMAGE_COUNT; i++){
            names.add(imageName + "_" +i);
        }
        return names;
    }
}
