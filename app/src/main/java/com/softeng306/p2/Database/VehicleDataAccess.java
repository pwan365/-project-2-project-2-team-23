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
import com.softeng306.p2.Models.Electric;
import com.softeng306.p2.Models.Hybrid;
import com.softeng306.p2.Models.Petrol;
import com.softeng306.p2.Models.Tag;
import com.softeng306.p2.Models.User;
import com.softeng306.p2.Models.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleDataAccess implements IVehicleDataAccess{
    private FirebaseFirestore _db;

    public VehicleDataAccess(){
        _db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getAllTags(OnGetTagListener listener) {
        List<Tag> tagList = new ArrayList<>();

        _db.collection("tags").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });
    }

    @Override
    public void getAllVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vList = new ArrayList<>();
        getElectricVehicles(new OnGetVehicleListener() {
            @Override
            public void onCallBack(List<Vehicle> vehicleList) {
                vList.addAll(vehicleList);
                getPetrolVehicles(new OnGetVehicleListener() {
                    @Override
                    public void onCallBack(List<Vehicle> vehicleList) {
                        vList.addAll(vehicleList);
                        getHybridVehicles(new OnGetVehicleListener() {
                            @Override
                            public void onCallBack(List<Vehicle> vehicleList) {
                                vList.addAll(vehicleList);
                                listener.onCallBack(vList);
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public void getElectricVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();

        _db.collection("electric").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (Vehicle vehicle: task.getResult().toObjects(Electric.class)){
                        vehicleList.add(vehicle);
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
    public void getPetrolVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();
        _db.collection("petrols").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (Vehicle vehicle: task.getResult().toObjects(Petrol.class)){
                        vehicleList.add(vehicle);
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
    public void getHybridVehicles(OnGetVehicleListener listener) {
        List<Vehicle> vehicleList = new ArrayList<>();

        _db.collection("hybrids").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (Vehicle vehicle: task.getResult().toObjects(Hybrid.class)){
                        vehicleList.add(vehicle);
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
    public void getVehicleByTag(List<Tag> tagList, OnGetVehicleListener listener) {
        List<Vehicle> vList = new ArrayList<>();
        getAllVehicles(new OnGetVehicleListener() {
            @Override
            public void onCallBack(List<Vehicle> vehicleList) {
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
            }
        });
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
