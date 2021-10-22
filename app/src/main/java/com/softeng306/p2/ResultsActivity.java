package com.softeng306.p2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.DataModel.Vehicle;
import com.softeng306.p2.Database.VehicleDataAccess;
import com.softeng306.p2.ViewModel.VehicleModel;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity{
    private String searchPhrase;
    private ArrayList<String> tags;
    private int CatColourInt;
    private ColorStateList CatColourState;
    private VehicleAdapter vehicleAdapter;
    private RecyclerView recyclerView;
    private LinearLayout noResultsContainer;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // initialise variables
        tags = new ArrayList<>();
        recyclerView = findViewById(R.id.results_recycler);
        noResultsContainer = findViewById(R.id.resultsNoResults);
        cardView = findViewById(R.id.results_load);
        CatColourInt = R.color.yellow;
        CatColourState = ColorStateList.valueOf(getResources().getColor(CatColourInt));

        fetchIntent();
        fetchVehicleData();
        initBackBtn();
        initNavigation();
    }

    private void fetchIntent() {
        // Receive data from intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        searchPhrase = extras.getString("searchPhrase");
        tags = extras.getStringArrayList("tags");
        setTitle();
    }

    private void setTitle() {
        // Set the list title
        TextView phraseTitle = findViewById(R.id.phraseText);
        phraseTitle.setText("\"" + searchPhrase + "\"");
    }

    private void initBackBtn() {
        // Initialize back button
        ImageButton backButton = findViewById(R.id.ResultsBackButton);
        backButton.setOnClickListener(v -> {
            noResultsContainer.setVisibility(View.GONE);
            finish();
            overridePendingTransition(R.anim.no_movement, R.anim.slide_to_bottom);
        });
    }

    private void initNavigation(){
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

    private void initLoading() {

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
        if(tags == null || tags.isEmpty()){
            vda.getVehicleByName(searchPhrase, vehicleList -> propagateListAdaptor(vehicleList));
        } else {
            vda.getVehicleByTagName(tags, "All", vehicleList -> {
                propagateListAdaptor(vehicleList);
                ResultsActivity.this.vehicleAdapter.getSearchFilter().filter(searchPhrase);
            });
        }
    }

    private void propagateListAdaptor(List<Vehicle> vehicleList) {
        if(vehicleList.isEmpty()){
            cardView.setVisibility(View.GONE);
            noResultsContainer.setVisibility(View.VISIBLE);
        } else {
            initLoading();
            noResultsContainer.setVisibility(View.GONE);
        }

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
}
