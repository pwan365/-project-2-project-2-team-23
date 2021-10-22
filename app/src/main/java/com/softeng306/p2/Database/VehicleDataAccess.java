package com.softeng306.p2.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306.p2.Helpers.VehicleComparator;
import com.softeng306.p2.Listeners.OnGetTagListener;
import com.softeng306.p2.Listeners.OnGetUserListener;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.DataModel.Electric;
import com.softeng306.p2.DataModel.Hybrid;
import com.softeng306.p2.DataModel.Petrol;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is the class that implements the methods in IVehicleDataAccess, used for retrieving data from
 * Firestore Firebase.
 * This is to be injected by VehicleService
 */
public class VehicleDataAccess implements IVehicleDataAccess{
    private FirebaseFirestore _db;

    /**
     * Constructor, initializes connection to database.
     */
    public VehicleDataAccess(){
        _db = FirebaseFirestore.getInstance();
    }

    /**
     * Get all the tags used by the vehicles
     * @param listener a listener that onCallback sends the list of tags.
     */
    @Override
    public void getAllTags(OnGetTagListener listener) {
        List<Tag> tagList = new ArrayList<>();

        _db.collection("tags").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (Tag tag: task.getResult().toObjects(Tag.class)){
                    tagList.add(tag);
                }
                listener.onCallBack(tagList);
            }
            else{
                System.out.println("Error retrieving tags");
            }
        });
    }

    /**
     * Get all the vehicles in the database
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getAllVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vList = new ArrayList<>();
        getElectricVehicles(vehicleList -> {
            vList.addAll(vehicleList);
            getPetrolVehicles(vehicleList1 -> {
                vList.addAll(vehicleList1);
                getHybridVehicles(vehicleList11 -> {
                    vList.addAll(vehicleList11);
                    listener.onCallBack(vList);
                });
            });
        });
    }

    /**
     * Get a list of vehicle based on what category is inputted
     * @param category a string that is the name of the category.
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getCategoryVehicles(String category, OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();

        _db.collection(category.toLowerCase(Locale.ROOT)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    switch (category) {
                    case "Electric":
                        for (Vehicle vehicle: task.getResult().toObjects(Electric.class)){
                            vehicleList.add(vehicle);
                        }
                        break;
                    case "Hybrid":
                        for (Vehicle vehicle: task.getResult().toObjects(Hybrid.class)){
                            vehicleList.add(vehicle);
                        }
                        break;
                    case "Petrol":
                        for (Vehicle vehicle: task.getResult().toObjects(Petrol.class)){
                            vehicleList.add(vehicle);
                        }
                        break;
                    }
                    listener.onCallBack(vehicleList);

                }
                else{
                    System.out.println("Error retrieving vehicles");
                }
            }
        });
    }

    /**
     * Get the list of electric vehicles in the database
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getElectricVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();

        _db.collection("electric").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (Vehicle vehicle: task.getResult().toObjects(Electric.class)){
                    vehicleList.add(vehicle);
                }
                listener.onCallBack(vehicleList);
            }
            else{
                System.out.println("Error retrieving vehicles");
            }
        });
    }

    /**
     * Get the list of petrol vehicles in the database
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getPetrolVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();
        _db.collection("petrol").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (Vehicle vehicle: task.getResult().toObjects(Petrol.class)){
                    vehicleList.add(vehicle);
                }
                listener.onCallBack(vehicleList);
            }
            else{
                System.out.println("Error retrieving vehicles");
            }
        });
    }

    /**
     * Get the list of hybrid vehicles in the database
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getHybridVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();

        _db.collection("hybrid").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (Vehicle vehicle: task.getResult().toObjects(Hybrid.class)){
                    vehicleList.add(vehicle);
                }
                listener.onCallBack(vehicleList);
            }
            else{
                System.out.println("Error retrieving vehicles");
            }
        });
    }

    /**
     * Get a list of vehicles based on the input tags given, returns the vehicles that has any of the
     * tag in the input list
     * @param tagList a list of tags
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getVehicleByTag(List<Tag> tagList, OnGetVehicleListener listener) {
        List<Vehicle> vList = new ArrayList<>();
        getAllVehicles(vehicleList -> {
            for (Vehicle v: vehicleList){
                boolean hasAllTags = true;
                for(Tag tag: tagList){
                    if (!v.hasTag(tag)){
                        hasAllTags = false;
                    }
                }
                if (hasAllTags){
                    vList.add(v);
                }
            }
            listener.onCallBack(vList);
        });
    }

    /**
     * Get a list of vehicles based on the input tags given, returns the vehicles that has any of the
     * tag in the input list
     * @param tagList a list of tag's strings
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getVehicleByTagName(List<String> tagList, String category, OnGetVehicleListener listener) {
        switch (category){
            case "Electric":
                getElectricVehicles(vehicleList -> {
                    List<Vehicle> vList = hasTags(tagList, vehicleList);
                    listener.onCallBack(vList);
                });
                break;
            case "Hybrid":
                getHybridVehicles(vehicleList -> {
                    List<Vehicle> vList = hasTags(tagList, vehicleList);
                    listener.onCallBack(vList);
                });
                break;
            case "Petrol":
                getPetrolVehicles(vehicleList -> {
                    List<Vehicle> vList = hasTags(tagList, vehicleList);
                    listener.onCallBack(vList);
                });
                break;
            case "All":
                getAllVehicles(vehicleList -> {
                    List<Vehicle> vList = hasTags(tagList, vehicleList);
                    listener.onCallBack(vList);
                });
                break;
        }
    }

    /**
     * Filters the vehicles that has all tags in the tagList
     * @param tagList a list of tag's strings
     * @param vehicleList list of input vehicles
     * @return list of filtered vehicles
     */
    public List<Vehicle> hasTags(List<String> tagList, List<Vehicle> vehicleList){
        List<Vehicle> vList = new ArrayList<>();
        for (Vehicle v: vehicleList){
            List<Vehicle> vTagList = new ArrayList<>();
            for(String tagName: tagList){
                if (v.hasTag(tagName)){
                    vTagList.add(v);
                }
            }
            vList = VehicleComparator.mergeVehicles(vTagList, vList);
        }
        return vList;
    }

    /**
     * Search for the vehicles that contains the input string
     * @param str input string
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getVehicleByName(String str, OnGetVehicleListener listener) {
        List<Vehicle> vList = new ArrayList<>();
        getAllVehicles(new OnGetVehicleListener() {
            @Override
            public void onCallBack(List<Vehicle> vehicleList) {
                for (Vehicle v: vehicleList){
                    if (v.containsString(str)){
                        vList.add(v);
                    }
                }
                listener.onCallBack(vList);
            }
        });
    }

    /**
     * Get a list of vehicles based on the input id
     * @param ids a list of ids
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getVehicleById(List<Integer> ids, OnGetVehicleListener listener) {
        List<Vehicle> vList = new ArrayList<>();
        getAllVehicles(new OnGetVehicleListener() {
            @Override
            public void onCallBack(List<Vehicle> vehicleList) {
                for (Vehicle v: vehicleList){
                    if (ids.contains(v.getId())){
                        vList.add(v);
                    }
                }
                listener.onCallBack(vList);
            }
        });
    }

    /**
     * Get the list of vehicles favourite by the user
     * @param listener a listener that onCallback sends the list of vehicles.
     */
    @Override
    public void getFavourites(OnGetUserListener listener) {
        _db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (User user: task.getResult().toObjects(User.class)){
                        listener.onCallBack(user);
                    }
                }
                else{
                    System.out.println("Error retrieving user");
                }
            }
        });
    }

    /**
     * Add a vehicle to the favourite list
     * @param vehicleId vehicle to add to favourite list
     */
    @Override
    public void addToFavourites(int vehicleId) {
        _db.collection("user").document("user").update(
                "favourites", FieldValue.arrayUnion(vehicleId));
    }

    /**
     * Remove a vehicle to the favourite list
     * @param vehicleId vehicle to remove from favourite list
     */
    @Override
    public void removeFromFavourites(int vehicleId) {
        _db.collection("user").document("user").update(
                "favourites", FieldValue.arrayRemove(vehicleId));
    }
}
