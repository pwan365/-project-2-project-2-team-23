package com.softeng306.p2.Helpers;

import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for comparing vehicles
 */
public class VehicleComparator {
    /**
     * Get the common vehicles between two input list
     * @param list1 first list
     * @param list2 second list
     * @return the common vehicles
     */
    public static List<Vehicle> commonVehicles(List<Vehicle> list1, List<Vehicle> list2){
        List<Vehicle> vehicles = new ArrayList<>();
        for (Vehicle v: list1){
            for(Vehicle v2: list2){
                if (v.getVehicleName().equals(v2.getVehicleName())){
                    vehicles.add(v);
                }
            }
        }
        return vehicles;
    }

    /**
     * Merge two list of vehicles without repeating vehicles in the output list
     * @param list1 first list
     * @param list2 second list
     * @return the merged list
     */
    public static List<Vehicle> mergeVehicles(List<Vehicle> list1, List<Vehicle> list2){
        List<Vehicle> vehicles = list1;
        for (Vehicle v: list2){
            boolean contains = false;
            for(Vehicle v2: list1){
                if (v.getVehicleName().equals(v2.getVehicleName())){
                    contains = true;
                }
            }
            if (!contains){
                vehicles.add(v);
            }
        }
        return vehicles;
    }
}
