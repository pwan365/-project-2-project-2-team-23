package com.softeng306.p2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.Database.VehicleDataAccess;
import com.softeng306.p2.Model.TagModel;
import com.softeng306.p2.Models.Tag;

import java.util.ArrayList;
import java.util.List;

public class RefineActivity extends AppCompatActivity {

    private ArrayList<TagModel> tagModels;
    private Button applyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refine);

        fetchTagData();

        // Initialize apply filters button
        applyBtn = findViewById(R.id.submitRefineBtn);
        applyBtn.setOnClickListener(v -> applyFilters());
    }

    private void fetchTagData() {
        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getAllTags(this::propagateAdaptor);
    }

    private void propagateAdaptor(List<Tag> tagsList) {
        TagAdapter tagAdapter;
        RecyclerView recyclerView = findViewById(R.id.tagsRecycler);

        // Create string array
        List<String> tagName = new ArrayList<>();
        List<String> tagType = new ArrayList<>();

        for(Tag tag : tagsList) {
            tagName.add(tag.getTagName());
            tagType.add(tag.getTagType());
        }

        // Initialize arraylist
        tagModels =  new ArrayList<>();
        for(int i = 0; i<tagsList.size();i++){
            TagModel model = new TagModel(tagName.get(i), tagType.get(i));
            tagModels.add(model);
        }

        // Design grid layout
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Initialize top adapter
        tagAdapter = new TagAdapter(RefineActivity.this, tagModels);
        recyclerView.setAdapter(tagAdapter);

    }

    private void applyFilters() {
        applyBtn.setBackgroundColor(getResources().getColor(R.color.orange));
    }
}