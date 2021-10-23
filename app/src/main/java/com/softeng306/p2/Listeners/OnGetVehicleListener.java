package com.softeng306.p2.Listeners;

import com.softeng306.p2.DataModel.Vehicle;

import java.util.List;

/**
 * Interface used to get a list of Vehicles
 */
public interface OnGetVehicleListener {
    void onCallBack(List<Vehicle> vehicleList);
}
