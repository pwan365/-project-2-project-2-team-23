package com.softeng306.p2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.DataModel.Vehicle;
import com.softeng306.p2.Database.VehicleDataAccess;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ResultsActivity extends AppCompatActivity {
    private String searchPhrase;
    private ArrayList<String> tags;
    private View bottomSheetView;
    private List<Integer> recyclerIds;
    private List<TagAdapter> adapters;
    private BottomSheetDialog dialog;
    private int CatColourInt;
    private ColorStateList CatColourState;
    private List<Vehicle> vehicleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // init arrays
        recyclerIds = new ArrayList<>();
        adapters = new ArrayList<>();
        tags = new ArrayList<>();
        vehicleList = new ArrayList<>();
        CatColourInt = R.color.yellow;
        CatColourState = ColorStateList.valueOf(getResources().getColor(CatColourInt));

        // Receive data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        searchPhrase = extras.getString("searchPhrase");
        tags = extras.getStringArrayList("tags");

        // Set the list title
        TextView phraseTitle = findViewById(R.id.phraseText);
        phraseTitle.setText("\"" + searchPhrase + "\"");

        fetchVehicleData();
        initRefineDialog();

        // Initialize refine button
        RelativeLayout refineBtn = findViewById(R.id.refineBtn);
        refineBtn.setVisibility(View.GONE);
        refineBtn.setOnClickListener(v -> dialog.show());

        // Initialize back button
        ImageButton listBackButton = findViewById(R.id.listBackButton);
        listBackButton.setOnClickListener(v -> GoBack());


        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    Intent homeIntent = new Intent(this, MainActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(homeIntent);
                    break;
                case R.id.searchIcon:
                    Intent searchIntent = new Intent(this, SearchActivity.class);
                    searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(searchIntent);
                    break;
                case R.id.favourtiesIcon:
                    Intent favIntent = new Intent(this, FavouritesActivity.class);
                    favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(favIntent);
                    break;
            }
            return false;
        });
    }

    // Returns to the main activity
    public void GoBack() {
        Intent intent = new Intent(this,  MainActivity.class);
        startActivity(intent);
    }

    private void fetchVehicleData() {
        VehicleDataAccess vda = new VehicleDataAccess();
        //vda.getVehicleByName(searchPhrase, vehicleList -> ResultsActivity.this.vehicleList.addAll(vehicleList));
        //vda.getVehicleByName(searchPhrase, vehicleList -> propagateListAdaptor(vehicleList));

        vda.getVehicleByName(searchPhrase, new OnGetVehicleListener() {
            @Override
            public void onCallBack(List<Vehicle> vehicleList) {
                ResultsActivity.this.vehicleList.addAll(vehicleList);
                if (tags != null && tags.size()>0) {
                    vda.getVehicleByTagName(tags, new OnGetVehicleListener() {
                        @Override
                        public void onCallBack(List<Vehicle> vehicleList) {
                            ResultsActivity.this.vehicleList.removeAll(vehicleList);
                            ResultsActivity.this.vehicleList.addAll(vehicleList);
                        }
                    });
                }
                propagateListAdaptor(ResultsActivity.this.vehicleList);
                System.out.println(ResultsActivity.this.vehicleList);
            }
        });
    }

    private void propagateListAdaptor(List<Vehicle> vehicleList) {
        VehicleAdapter vehicleAdapter;
        RecyclerView recyclerView = findViewById(R.id.recycler);

        // Create string array
        List<String> vehicleName = new ArrayList<>();
        List<Float> vehiclePrice = new ArrayList<>();

        for(Vehicle vehicle : vehicleList) {
            vehicleName.add(vehicle.getVehicleName());
            vehiclePrice.add(vehicle.getPrice());
        }

        // Initialize arraylist
        ArrayList<VehicleModel> vehicleModels = new ArrayList<>();
        for(int i = 0; i<vehicleList.size();i++){
            VehicleModel model = new VehicleModel(vehicleName.get(i), vehiclePrice.get(i));
            vehicleModels.add(model);
        }

        // Design grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Initialize top adapter
        vehicleAdapter = new VehicleAdapter(ResultsActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);

    }

    private void initRefineDialog() {
        dialog = new BottomSheetDialog(ResultsActivity.this, R.style.BottomSheetTheme);
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_refine, findViewById(R.id.bottomSheetContainer));

        Button submitRefineBtn = bottomSheetView.findViewById(R.id.submitRefineBtn);
        submitRefineBtn.setBackgroundTintList(CatColourState);
        submitRefineBtn.setOnClickListener(view -> {
            List<String> onTags = new ArrayList<>();
            for (TagAdapter a : adapters) {
                for (String s : a.getOnTags()) {
                    onTags.add(s);
                }
            }
            if(onTags.isEmpty()) {
                Toast.makeText(getApplicationContext(),"Please select at least one tag",Toast. LENGTH_SHORT).show();
            } else {
                VehicleDataAccess vda = new VehicleDataAccess();
                vda.getVehicleByTagName(onTags, ResultsActivity.this::propagateListAdaptor);
                dialog.hide();
            }
        });

        dialog.setContentView(bottomSheetView);

        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getAllTags(tagList -> {
            ArrayList<List<String>> sortedTags = listTagTypes(tagList);

            for(List<String> typeTagList : sortedTags) {
                String type = typeTagList.get(0);
                TextView typeTitle = new TextView(ResultsActivity.this);
                typeTitle.setText(type);
                typeTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ((LinearLayout) bottomSheetView).addView(typeTitle, 2);

                propagateTagAdaptor(typeTagList);

            }
        });

    }

    private ArrayList<List<String>> listTagTypes(List<Tag> tagsList) {

        // Create string hash set of types
        LinkedHashSet<String> tagTypes = new LinkedHashSet<>();
        for(Tag tag : tagsList) {
            tagTypes.add(tag.getTagType());
        }

        ArrayList<List<String>> sortedTags = new ArrayList<>();
        // Sort tags by type
        for(String type: tagTypes) {
            List<String> tagNames = new ArrayList<>();
            tagNames.add(type);
            for (Tag tag : tagsList) {
                if (tag.getTagType().equals(type)) {
                    tagNames.add(tag.getTagName());
                }
            }
            sortedTags.add(tagNames);
        }

        return sortedTags;
    }

    private void propagateTagAdaptor(List<String> tagNames) {
        RecyclerView tagRecyclerView = new RecyclerView(ResultsActivity.this);
        int id = View.generateViewId();
        tagRecyclerView.setId(id);
        recyclerIds.add(id);
        TagAdapter tagAdapter;

        // Initialize arraylist
        ArrayList<TagModel> tagModels = new ArrayList<>();
        for(int i = 1; i<tagNames.size();i++){
            TagModel model = new TagModel(tagNames.get(i));
            tagModels.add(model);
        }

        // Design horizontal layout
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize adapter
        tagAdapter = new TagAdapter(ResultsActivity.this, tagModels, CatColourState);
        adapters.add(tagAdapter);
        tagRecyclerView.setAdapter(tagAdapter);

        ((LinearLayout) bottomSheetView).addView(tagRecyclerView, 3);
    }
}
