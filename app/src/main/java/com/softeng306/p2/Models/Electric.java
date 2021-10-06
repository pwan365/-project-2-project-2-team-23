package com.softeng306.p2.Models;

public class Electric extends Vehicle{
    private String batteryCapacity;
    private String chargingTime;
    private String travelDistance;

    public Electric(int id, String name) {
        super(id, name);
    }

    public Electric(){
        super();
    }

    public String getBatteryCapacity(){
        return batteryCapacity;
    }

    public void setBatteryCapacity(String i){
        batteryCapacity = i;
    }

    public String getChargingTime(){
        return chargingTime;
    }

    public void setChargingTime(String i){
        chargingTime = i;
    }

    public String getTravelDistance(){
        return travelDistance;
    }

    public void setTravelDistance(String i){
        travelDistance = i;
    }
}
