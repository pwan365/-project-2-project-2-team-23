package com.softeng306.p2.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Vehicle {
    private int id;
    private String vehicleName;
    private Map<String, String> tags = new HashMap<>();
    private float price;
    private String dimension;
    private float weight;
    private String description;
    private int manufacturedDate;
    private List<String> images = new ArrayList<>();

    public Vehicle(int id, String name){
        this.id = id;
        vehicleName = name;
    }

    public Vehicle(){}

    public int getId(){
        return id;
    }

    public String getVehicleName(){
        return vehicleName;
    }

    public void addTag(Tag newTag){
        String tagName = newTag.getTagName();
        String tagType = newTag.getTagType();
        tags.put(tagName, tagType);
    }

    public Map<String, String> getTags(){
        return tags;
    }

    public boolean hasTag(Tag tag){
        if (tags.get(tag.getTagName()) == null){
            return false;
        }
        return true;
    }

    public float getPrice(){
        return price;
    }

    public void setPrice(float newPrice){
        price = newPrice;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String newDimension){
        dimension = newDimension;
    }

    public float getWeight(){
        return weight;
    }

    public void setWeight(float newWeight){
        weight = newWeight;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String newDescription){
        description = newDescription;
    }

    public int getManufacturedDate(){
        return manufacturedDate;
    }

    public void setManufacturedDate(int i){
        manufacturedDate = i;
    }

    public List<String> getImages(){
        return images;
    }

    public void setImages(List<String> imageNames){
        images = imageNames;
    }
}
