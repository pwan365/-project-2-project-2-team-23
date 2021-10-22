package com.softeng306.p2.DataModel;

/**
 * Object to represent a hybrid vehicle
 */
public class Hybrid extends Vehicle{
    private boolean isPHEV;
    private int chargingTime;

    /**
     * Constructor
     * @param id id for the vehicle
     * @param name name of the vehicle
     */
    public Hybrid(int id, String name) {
        super(id, name);
    }

    /**
     * empty constructor for firebase
     */
    public Hybrid(){
        super();
    }

    /**
     * Getter for PHEV
     */
    public boolean getIsPHEV(){
        return isPHEV;
    }

    /**
     * Setter for PHEV
     * @param b PHEV
     */
    public void setIsPHEV(boolean b){
        isPHEV = b;
    }

    /**
     * Getter for charging time
     */
    public int getChargingTime(){
        return chargingTime;
    }

    /**
     * Setter for charging time
     * @param i charging time
     */
    public void setChargingTime(int i){
        chargingTime = i;
    }
}
