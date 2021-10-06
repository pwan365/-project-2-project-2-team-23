package com.softeng306.p2.Database;

import com.softeng306.p2.Listeners.OnGetTagListener;
import com.softeng306.p2.Listeners.OnGetVehicleListener;

public interface IVehicleDataAccess {
    public void getAllTags(OnGetTagListener listener);
    public void getAllVehicles(OnGetVehicleListener listener);
    public void getElectricVehicles(OnGetVehicleListener listener);
    public void getPetrolVehicles(OnGetVehicleListener listener);
    public void getHybridVehicles(OnGetVehicleListener listener);
}
