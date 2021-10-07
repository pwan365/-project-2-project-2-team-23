package com.softeng306.p2.Database;

import com.softeng306.p2.Listeners.OnGetTagListener;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.Models.Tag;

import java.util.List;

public interface IVehicleDataAccess {
    void getAllTags(OnGetTagListener listener);
    void getAllVehicles(OnGetVehicleListener listener);
    void getElectricVehicles(OnGetVehicleListener listener);
    void getPetrolVehicles(OnGetVehicleListener listener);
    void getHybridVehicles(OnGetVehicleListener listener);
    void getVehicleByTag(List<Tag> tagList, OnGetVehicleListener listener);
    void getVehicleByName(String str, OnGetVehicleListener listener);
}
