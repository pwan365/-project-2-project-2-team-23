package com.softeng306.p2.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

public class VehicleDataAccess implements IVehicleDataAccess{
    private FirebaseFirestore _db;

    public VehicleDataAccess(){
        _db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getAllTags(OnGetTagListener listener) {
        List<Tag> tagList = new ArrayList<>();

        _db.collection("tags").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (Tag tag: task.getResult().toObjects(Tag.class)){
                    tagList.add(tag);
                    //System.out.println("tag " +tag.getId() + " "+ tag.getTagName()+ " "+ tag.getTagType());
                }
                listener.onCallBack(tagList);
            }
            else{
                System.out.println("Error retrieving tags");
            }
        });
    }

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

    // Kayla added - not sure if I can do this but it works
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

    // Added by Kayla - to get by tag but by string instead of Tag object
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
                getAllVehicles(new OnGetVehicleListener() {
                    @Override
                    public void onCallBack(List<Vehicle> vehicleList) {
                        List<Vehicle> vList = hasTags(tagList, vehicleList);
                        listener.onCallBack(vList);
                    }
                });
                break;
        }
    }

    public List<Vehicle> hasTags(List<String> tagList, List<Vehicle> vehicleList){
        List<Vehicle> vList = new ArrayList<>();
        for (Vehicle v: vehicleList){
            boolean hasAllTags = true;
            for(String tagName: tagList){
                if (!v.hasTag(tagName)){
                    hasAllTags = false;
                }
            }
            if (hasAllTags){
                vList.add(v);
            }
        }
        return vList;
    }

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

    @Override
    public void addToFavourites(int vehicleId) {
        _db.collection("user").document("user").update(
                "favourites", FieldValue.arrayUnion(vehicleId));
    }

    @Override
    public void removeFromFavourites(int vehicleId) {
        _db.collection("user").document("user").update(
                "favourites", FieldValue.arrayRemove(vehicleId));
    }
}
