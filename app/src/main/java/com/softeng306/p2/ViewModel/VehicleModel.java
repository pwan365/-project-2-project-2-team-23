package com.softeng306.p2.ViewModel;

public class VehicleModel {
    String vName;
    Float vPrice;

    public VehicleModel(String vName, Float vPrice){
        this.vName = vName;
        this.vPrice = vPrice;
    };

    public String getVName(){
        return vName;
    }

    public Float getVPrice(){
        return vPrice;
    }
}
