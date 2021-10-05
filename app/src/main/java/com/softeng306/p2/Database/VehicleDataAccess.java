package com.softeng306.p2.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.softeng306.p2.Listeners.OnGetTagListener;
import com.softeng306.p2.Models.Tag;

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
}
