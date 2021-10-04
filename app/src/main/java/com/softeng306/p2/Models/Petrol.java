package com.softeng306.p2.Models;

public class Petrol extends Vehicle{
    private int tankCapacity;

    public Petrol(int id, String name) {
        super(id, name);
    }

    public int getTankCapacity(){
        return tankCapacity;
    }

    public void setTankCapacity(int i){
        tankCapacity = i;
    }
}
