package com.softeng306.p2.Models;

public class Hybrid extends Vehicle{
    private boolean isPHEV;
    private int chargingTime;

    public Hybrid(int id, String name) {
        super(id, name);
    }

    public Hybrid(){
        super();
    }

    public boolean getIsPHEV(){
        return isPHEV;
    }

    public void setIsPHEV(boolean b){
        isPHEV = b;
    }

    public int getChargingTime(){
        return chargingTime;
    }

    public void setChargingTime(int i){
        chargingTime = i;
    }
}
