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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

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
import com.softeng306.p2.Model.TagModel;
import com.softeng306.p2.Model.VehicleModel;
import com.softeng306.p2.Models.Tag;
import com.softeng306.p2.Models.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private SearchView searchBar;
    private ImageView closeSearch;
    private String categoryName;
    private View bottomSheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // find id references
        ImageButton listSearchButton = findViewById(R.id.listSearchButton);
        searchBar = findViewById(R.id.ListSearchBar);
        closeSearch = findViewById(R.id.closeSearchArea);

        // Receive data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        categoryName = extras.getString("category");
        String categorySubtitle = extras.getString("categorySubtitle");
        ColorStateList categoryColour = extras.getParcelable("categoryColour");

        fetchVehicleData();

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
        listHeading.setBackgroundColor(categoryColour.getDefaultColor());

        // Set the refine colour
        RelativeLayout refineBtn = findViewById(R.id.refineBtn);
        refineBtn.setBackgroundTintList(categoryColour);

        // Initialize back button
        ImageButton listBackButton = findViewById(R.id.listBackButton);
        listBackButton.setOnClickListener(v -> GoBack());

        // Initialize refine button
        refineBtn.setOnClickListener(v -> {
            showRefineDialog();
        });

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
        Log.i("MainActivity", "Searching for " + phrase);
        /* TO DO */
    }

    private void fetchVehicleData() {
        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getCategoryVehicles(categoryName, this::propagateAdaptor);
    }

    private void propagateAdaptor(List<Vehicle> vehicleList) {
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

    private void showRefineDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(ListActivity.this, R.style.BottomSheetTheme);
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_refine, findViewById(R.id.bottomSheetContainer));
        //bottomSheetView.findViewById(R.id.submitRefineBtn).setOnClickListener(view -> /*TO DO*/ );
        dialog.setContentView(bottomSheetView);
        dialog.show();

        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getAllTags(this::propagateTagAdaptor);
    }

    private void propagateTagAdaptor(List<Tag> tagsList) {

        TagAdapter tagAdapter;
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.tagsRecycler);

        // Create string array
        List<String> tagName = new ArrayList<>();
        List<String> tagType = new ArrayList<>();

        for(Tag tag : tagsList) {
            tagName.add(tag.getTagName());
            tagType.add(tag.getTagType());
        }

        // Initialize arraylist
        ArrayList<TagModel> tagModels = new ArrayList<>();
        for(int i = 0; i<tagsList.size();i++){
            TagModel model = new TagModel(tagName.get(i), tagType.get(i));
            tagModels.add(model);
        }

        // Design grid layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Initialize top adapter
        tagAdapter = new TagAdapter(ListActivity.this, tagModels);
        recyclerView.setAdapter(tagAdapter);

        TextView typeTitle = bottomSheetView.findViewById(R.id.TypeTitle1);
        typeTitle.setText(tagModels.get(0).getTType());

    }

}