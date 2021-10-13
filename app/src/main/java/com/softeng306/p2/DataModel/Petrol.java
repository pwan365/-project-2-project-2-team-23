package com.softeng306.p2.DataModel;

public class Petrol extends Vehicle{
    private float tankCapacity;

    public Petrol(int id, String name) {
        super(id, name);
    }

    public Petrol(){
        super();
    }

    public float getTankCapacity(){
        return tankCapacity;
    }

    public void setTankCapacity(float i){
        tankCapacity = i;
    }
}
