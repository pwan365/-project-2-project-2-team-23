package com.softeng306.p2;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
import com.softeng306.p2.Database.VehicleDataAccess;
import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private SearchView searchBar;
    private ImageView closeSearch;
    private String categoryName;
    private View bottomSheetView;
    private List<Integer> recyclerIds;
    private List<TagAdapter> adapters;
    private BottomSheetDialog dialog;
    private int CatColourInt;
    private ColorStateList CatColourState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // find id references
        ImageButton listSearchButton = findViewById(R.id.listSearchButton);
        searchBar = findViewById(R.id.ListSearchBar);
        closeSearch = findViewById(R.id.closeSearchArea);

        // init arrays
        recyclerIds = new ArrayList<>();
        adapters = new ArrayList<>();

        // Receive data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        categoryName = extras.getString("category");
        String categorySubtitle = extras.getString("categorySubtitle");
        CatColourState = extras.getParcelable("categoryColour");

        fetchVehicleData();
        initRefineDialog();

        // Set the list title
        TextView catTitle = findViewById(R.id.ListTitle);
        catTitle.setText(categoryName);

        // Set the list subtitle
        TextView catSubtitle = findViewById(R.id.ListSubtitle);
        catSubtitle.setText(categorySubtitle);

        // Set the list image
        ImageView catImg = findViewById(R.id.ListHeaderImage);
        int resId = getResources().getIdentifier(categoryName.toLowerCase(Locale.ROOT),"drawable",ListActivity.this.getPackageName());
        Drawable d = ListActivity.this.getResources().getDrawable(resId);
        catImg.setImageDrawable(d);

        // Set the heading colour
        RelativeLayout listHeading = findViewById(R.id.ListHeader);
        CatColourInt = CatColourState.getDefaultColor();
        listHeading.setBackgroundColor(CatColourInt);

        // Set the refine colour
        RelativeLayout refineBtn = findViewById(R.id.refineBtn);
        refineBtn.setBackgroundTintList(CatColourState);

        // Initialize refine button
        refineBtn.setOnClickListener(v -> dialog.show());

        // Initialize back button
        ImageButton listBackButton = findViewById(R.id.listBackButton);
        listBackButton.setOnClickListener(v -> GoBack());

        // Initialize search button
        listSearchButton.setOnClickListener(v -> {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            closeSearch.setVisibility(View.VISIBLE);
        });

        // Initialize close search view
        closeSearch.setOnClickListener(v -> {
            searchBar.setVisibility(View.INVISIBLE);
            closeSearch.setVisibility(View.INVISIBLE);
        });

        // Set up the search bar
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                SearchEventHandler(searchInput);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });

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

    // Open search activity with results of the phrase inputted by user
    public void SearchEventHandler(String phrase) {
        Intent listIntent = new Intent(this, ResultsActivity.class);
        listIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        listIntent.putExtra("searchPhrase", phrase);
        startActivity(listIntent);
    }

    private void fetchVehicleData() {
        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getCategoryVehicles(categoryName, this::propagateListAdaptor);
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
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Initialize top adapter
        vehicleAdapter = new VehicleAdapter(ListActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);

    }

    private void initRefineDialog() {
        dialog = new BottomSheetDialog(ListActivity.this, R.style.BottomSheetTheme);
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
                vda.getVehicleByTagName(onTags, ListActivity.this::propagateListAdaptor);
                dialog.hide();
            }
        });

        dialog.setContentView(bottomSheetView);

        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getAllTags(tagList -> {
            ArrayList<List<String>> sortedTags = listTagTypes(tagList);

            for(List<String> typeTagList : sortedTags) {
                String type = typeTagList.get(0);
                TextView typeTitle = new TextView(ListActivity.this);
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
        RecyclerView tagRecyclerView = new RecyclerView(ListActivity.this);
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
        tagAdapter = new TagAdapter(ListActivity.this, tagModels, CatColourState);
        adapters.add(tagAdapter);
        tagRecyclerView.setAdapter(tagAdapter);

        ((LinearLayout) bottomSheetView).addView(tagRecyclerView, 3);
    }
}