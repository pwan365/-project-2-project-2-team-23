package com.softeng306.p2.DataModel;

/**
 * Object to represent an electric vehicle
 */
public class Electric extends Vehicle{
    private String batteryCapacity;
    private String chargingTime;
    private String travelDistance;

    /**
     * Constructor
     * @param id id for the vehicle
     * @param name name of the vehicle
     */
    public Electric(int id, String name) {
        super(id, name);
    }

    /**
     * empty constructor for firebase
     */
    public Electric(){
        super();
    }

    /**
     * Getter for battery capacity
     */
    public String getBatteryCapacity(){
        return batteryCapacity;
    }

    /**
     * Setter for battery capacity
     * @param i battery capacity
     */
    public void setBatteryCapacity(String i){
        batteryCapacity = i;
    }

    /**
     * Getter for charging time
     */
    public String getChargingTime(){
        return chargingTime;
    }

    /**
     * Setter for charging time
     * @param i charging time
     */
    public void setChargingTime(String i){
        chargingTime = i;
    }

    /**
     * Getter for travel distance
     */
    public String getTravelDistance(){
        return travelDistance;
    }

    /**
     * Setter for travel distance
     * @param i travel distance
     */
    public void setTravelDistance(String i){
        travelDistance = i;
    }
}
