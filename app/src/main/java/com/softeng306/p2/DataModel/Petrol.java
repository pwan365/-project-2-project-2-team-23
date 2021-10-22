package com.softeng306.p2.DataModel;

/**
 * Object to represent a petrol vehicle
 */
public class Petrol extends Vehicle{
    private float tankCapacity;

    /**
     * Constructor
     * @param id id for the vehicle
     * @param name name of the vehicle
     */
    public Petrol(int id, String name) {
        super(id, name);
    }

    /**
     * empty constructor for firebase
     */
    public Petrol(){
        super();
    }

    /**
     * Getter for tank capacity
     */
    public float getTankCapacity(){
        return tankCapacity;
    }

    /**
     * Setter for tank capacity
     * @param i tank capacity
     */
    public void setTankCapacity(float i){
        tankCapacity = i;
    }
}
