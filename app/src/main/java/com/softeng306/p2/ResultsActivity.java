package com.softeng306.p2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import androidx.cardview.widget.CardView;
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
import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private String searchPhrase;
    private ArrayList<String> tags;
    private View bottomSheetView;
    private List<Integer> recyclerIds;
    private List<TagAdapter> adapters;
    private BottomSheetDialog dialog;
    private int CatColourInt;
    private ColorStateList CatColourState;
    private VehicleAdapter vehicleAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // init arrays
        recyclerIds = new ArrayList<>();
        adapters = new ArrayList<>();
        tags = new ArrayList<>();
        recyclerView = findViewById(R.id.results_recycler);
        CatColourInt = R.color.yellow;
        CatColourState = ColorStateList.valueOf(getResources().getColor(CatColourInt));

        initLoading();

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
        ImageButton backButton = findViewById(R.id.ResultsBackButton);
        backButton.setOnClickListener(v -> finish());


        // Initialise the navigation buttons
        initNavigation();
    }

    private void initNavigation(){
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

    private void initLoading() {
        CardView cardView = findViewById(R.id.results_load);
        recyclerView.setVisibility(View.INVISIBLE);
        cardView.postDelayed(new Runnable() {
            public void run() {
                cardView.animate()
                        .translationY(cardView.getHeight())
                        .alpha(0.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 1000);
    }

    private void fetchVehicleData() {
        VehicleDataAccess vda = new VehicleDataAccess();
        if(tags == null){
            vda.getVehicleByName(searchPhrase, vehicleList -> propagateListAdaptor(vehicleList));
        } else {
            vda.getVehicleByTagName(tags, "All", vehicleList -> {
                propagateListAdaptor(vehicleList);
                ResultsActivity.this.vehicleAdapter.getSearchFilter().filter(searchPhrase);
            });
        }
    }

    private void propagateListAdaptor(List<Vehicle> vehicleList) {
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
                vda.getVehicleByTagName(onTags, "All", ResultsActivity.this::propagateListAdaptor);
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
