package com.softeng306.p2;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    private ImageButton listSearchButton;
    private String categoryName, categorySubtitle;
    private View bottomSheetView;
    private List<TagAdapter> adapters;
    private BottomSheetDialog dialog;
    private int CatColourInt;
    private ColorStateList CatColourState;
    private VehicleAdapter vehicleAdapter;
    private LinearLayout listNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Find id references
        listSearchButton = findViewById(R.id.listSearchButton);
        searchBar = findViewById(R.id.ListSearchBar);
        closeSearch = findViewById(R.id.closeSearchArea);
        listNoResults = findViewById(R.id.listNoResults);

        // Initialise arrays
        adapters = new ArrayList<>();

        fetchIntent();
        fetchVehicleData();
        initRefineDialog();
        initHeaderStyling();
        setupRefineBtn();
        SearchSetup();
        initNavigation();
        setUpNoResults();
    }

    private void setUpNoResults() {
        Button listResetBtn = findViewById(R.id.listResetBtn);
        listResetBtn.setBackgroundTintList(CatColourState);
        listResetBtn.setOnClickListener(view -> {
            fetchVehicleData();
            initRefineDialog();
        });
    }

    private void fetchIntent() {
        // Receive data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        categoryName = extras.getString("category");
        categorySubtitle = extras.getString("categorySubtitle");
        CatColourState = extras.getParcelable("categoryColour");
        CatColourInt = CatColourState.getDefaultColor();
    }

    private void setupRefineBtn() {
        // Set the refine colour
        RelativeLayout refineBtn = findViewById(R.id.refineBtn);
        refineBtn.setBackgroundTintList(CatColourState);

        // Initialize refine button
        refineBtn.setOnClickListener(v -> dialog.show());

        // Initialize back button
        ImageButton listBackButton = findViewById(R.id.listBackButton);
        listBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_movement, R.anim.slide_to_bottom);
            }
        });

        SearchSetup();
        initNavigation();
    }

    private void initHeaderStyling() {
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
        RelativeLayout listActionBar = findViewById(R.id.ListActionBar);
        listHeading.setBackgroundColor(CatColourInt);
        listActionBar.setBackgroundColor(CatColourInt);

        // Initialize back button
        ImageButton listBackButton = findViewById(R.id.listBackButton);
        listBackButton.setOnClickListener(v -> finish());
    }

    public void initNavigation() {
        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    finish();
                    overridePendingTransition(0, R.anim.slide_to_bottom);
                    break;
                case R.id.searchIcon:
                    Intent searchIntent = new Intent(this, SearchActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(0, R.anim.slide_to_bottom);
                    break;
                case R.id.favourtiesIcon:
                    Intent favIntent = new Intent(this, FavouritesActivity.class);
                    startActivity(favIntent);
                    overridePendingTransition(0, R.anim.slide_to_bottom);
                    break;
            }
            return false;
        });
    }

    public void SearchSetup(){
        // Initialize search button
        listSearchButton.setOnClickListener(v -> {
            closeSearch.setVisibility(View.VISIBLE);
            closeSearch.bringToFront();
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            searchBar.bringToFront();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);

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
                ListActivity.this.vehicleAdapter.getSearchFilter().filter(searchInput);
                searchBar.setVisibility(View.INVISIBLE);
                closeSearch.setVisibility(View.INVISIBLE);
                if (ListActivity.this.vehicleAdapter.getItemCount() == 0){
                    listNoResults.setVisibility(View.VISIBLE);
                } else {
                    listNoResults.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                ListActivity.this.vehicleAdapter.getSearchFilter().filter(searchInput);
                return false;
            }
        });
    }

    private void fetchVehicleData() {
        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getCategoryVehicles(categoryName, this::propagateListAdaptor);
    }

    private void propagateListAdaptor(List<Vehicle> vehicleList){

        if(vehicleList.isEmpty()){
            listNoResults.setVisibility(View.VISIBLE);
        } else {
            listNoResults.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = findViewById(R.id.list_recycler);
        VehicleDataAccess vda = new VehicleDataAccess();

        // Create string array
        List<String> vehicleName = new ArrayList<>();
        List<Float> vehiclePrice = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {
            vehicleName.add(vehicle.getVehicleName());
            vehiclePrice.add(vehicle.getPrice());
        }

        // Initialize arraylist
        ArrayList<VehicleModel> vehicleModels = new ArrayList<>();
        for (int i = 0; i < vehicleList.size(); i++) {
            VehicleModel model = new VehicleModel(vehicleName.get(i), vehiclePrice.get(i));
            vehicleModels.add(model);
        }

        // Design grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Initialize adapter
        vehicleAdapter = new VehicleAdapter(ListActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);
    }

    private void initRefineDialog() {
        dialog = new BottomSheetDialog(ListActivity.this, R.style.BottomSheetTheme);
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_refine, findViewById(R.id.bottomSheetContainer));
        // Submit changes to the recycler view from tags chosen
        refineSubmit();
        dialog.setContentView(bottomSheetView);

        FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getAllTags(tagList -> {
            ArrayList<List<String>> sortedTags = listTagTypes(tagList);

            for(List<String> typeTagList : sortedTags) {
                String type = typeTagList.get(0);
                System.out.println(type);
                if (!"VEHICLE TYPE".equals(type)) {
                    TextView typeTitle = new TextView(ListActivity.this);
                    typeTitle.setText(type);
                    typeTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout) bottomSheetView).addView(typeTitle, 2);

                    propagateTagAdaptor(typeTagList);
                }

            }
        });

    }

    private void refineSubmit() {
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
                vda.getVehicleByTagName(onTags, categoryName, ListActivity.this::propagateListAdaptor);
                dialog.hide();
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