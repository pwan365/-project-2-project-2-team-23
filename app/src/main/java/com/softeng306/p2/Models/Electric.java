package com.softeng306.p2.Models;

public class Electric extends Vehicle{
    private int batteryCapacity;
    private int chargingTime;
    private int travelDistance;

    public Electric(int id, String name) {
        super(id, name);
    }

    public int getBatteryCapacity(){
        return batteryCapacity;
    }

    public void setBatteryCapacity(int i){
        batteryCapacity = i;
    }

    public int getChargingTime(){
        return chargingTime;
    }

    public void setChargingTime(int i){
        chargingTime = i;
    }

    public int getTravelDistance(){
        return travelDistance;
    }

    public void setTravelDistance(int i){
        travelDistance = i;
    }
}
