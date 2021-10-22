package com.softeng306.p2.ViewModel;

/**
 * VehicleModel is used to create objects used to view a vehicle's price and name
 */
public class VehicleModel {
    String vName;
    Float vPrice;

    // Initialise object
    public VehicleModel(String vName, Float vPrice){
        this.vName = vName;
        this.vPrice = vPrice;
    }

    // Getter methods
    public String getVName(){
        return vName;
    }
    public Float getVPrice(){
        return vPrice;
    }
}
