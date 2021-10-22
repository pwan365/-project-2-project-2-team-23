package com.softeng306.p2.Database;

import com.softeng306.p2.Listeners.OnGetTagListener;
import com.softeng306.p2.Listeners.OnGetUserListener;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.DataModel.Tag;

import java.util.List;

/**
 * Interface to be implemented, used for accessing the database
 */
public interface IVehicleDataAccess {
    void getAllTags(OnGetTagListener listener);
    void getAllVehicles(OnGetVehicleListener listener);
    void getCategoryVehicles(String category, OnGetVehicleListener listener);
    void getElectricVehicles(OnGetVehicleListener listener);
    void getPetrolVehicles(OnGetVehicleListener listener);
    void getHybridVehicles(OnGetVehicleListener listener);
    void getVehicleByTag(List<Tag> tagList, OnGetVehicleListener listener);
    void getVehicleByTagName(List<String> tagList, String category, OnGetVehicleListener listener);
    void getVehicleByName(String str, OnGetVehicleListener listener);
    void getVehicleById(List<Integer> ids, OnGetVehicleListener listener);
    void getFavourites(OnGetUserListener listener);
    void addToFavourites(int vehicleId);
    void removeFromFavourites(int vehicleId);
}
