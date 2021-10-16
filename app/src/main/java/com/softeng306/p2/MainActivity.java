package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.TopAdapter;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Helpers.VehicleComparator;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.ViewModel.TopModel;
import com.softeng306.p2.DataModel.Vehicle;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CoreActivity {
    static class ViewHolder {
        private CardView CatElectric, CatHybrid, CatPetrol;
        private SearchView SearchBar;
        private RecyclerView recyclerView;
        private BottomNavigationView bottomNavigationView;
    }

    //Initialize variable
    RecyclerView recyclerView;
    ArrayList<TopModel> topModels;
    TopAdapter topAdapter;
    ViewHolder vh;

    IVehicleDataAccess vda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLoading();
        VehicleService.getInstance().InjectService(this);

        vda.getVehicleByName("c", vehicleList -> {
            vda.getElectricVehicles(vehicleList2 ->{
                List<Vehicle> l1 = VehicleComparator.commonVehicles(vehicleList, vehicleList2);
                for (Vehicle v: l1){
                    System.out.println(v.getVehicleName());
                }
            });
        });

        // Initialise views for future references
        vh = new ViewHolder();
        vh.SearchBar = findViewById(R.id.SearchBar);
        vh.CatElectric = findViewById(R.id.Electric);
        vh.CatHybrid = findViewById(R.id.Hybrid);
        vh.CatPetrol = findViewById(R.id.Petrol);
        vh.recyclerView = findViewById(R.id.recycler_view);
        vh.bottomNavigationView = findViewById(R.id.nav_bar);

        // Create integer array
        fetchTopPickData();


        // Set up the search bar
        vh.SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        // Initialise the category buttons
        vh.CatElectric.setOnClickListener(this::CategoryEventHandler);
        vh.CatHybrid.setOnClickListener(this::CategoryEventHandler);
        vh.CatPetrol.setOnClickListener(this::CategoryEventHandler);

        // Initialise the navigation buttons
        Menu menu = vh.bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        vh.bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    break;
                case R.id.searchIcon:
                    Intent searchIntent = new Intent(this, SearchActivity.class);
                    searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    break;
                case R.id.favourtiesIcon:
                    Intent favIntent = new Intent(this, FavouritesActivity.class);
                    favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(favIntent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    break;
            }
        return false;
        });
    }

    private void initLoading() {
        CardView cardView = findViewById(R.id.main_load);
        LinearLayout topPickContainer = findViewById(R.id.TopPicksContainer);
        topPickContainer.setVisibility(View.INVISIBLE);
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
                                topPickContainer.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 2000);
    }

    // Open search activity with results of the phrase inputted by user
    public void SearchEventHandler(String phrase) {
        Intent listIntent = new Intent(this, ResultsActivity.class);
        listIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        listIntent.putExtra("searchPhrase", phrase);
        startActivity(listIntent);
    }

    // Open list activity based on the category clicked on
    public void CategoryEventHandler(View v) {
        CardView category = (CardView) v;
        Log.i("MainActivity", "Opening " + category.getContentDescription());
        Intent listIntent = new Intent(this, ListActivity.class);
        /*listIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/

        Bundle extras = new Bundle();
        int intName= category.getId();
        extras.putString("category", getResources().getResourceEntryName(intName));
        extras.putString("categorySubtitle", (String) category.getContentDescription());
        extras.putParcelable("categoryColour", category.getCardBackgroundColor());
        listIntent.putExtras(extras);
        startActivity(listIntent);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.no_movement);
    }

    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }

    private void fetchTopPickData(){vda.getAllVehicles(new OnGetVehicleListener() {
        @Override
        public void onCallBack(List<Vehicle> vehicleList) {
            int topPicks = 8;
            int id;
            topModels = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            Random rand = new Random();
            while (ids.size() != topPicks) {
                id = rand.nextInt(vehicleList.size());
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            }

            List<Vehicle> vehicles = new ArrayList<>();
            for (int i : ids) {
                vehicles.add(vehicleList.get(i));
            }

            for (Vehicle vehicle : vehicles) {
                TopModel model = new TopModel(vehicle.getVehicleName());
                topModels.add(model);
            }

            // Design Horizontal Layout
            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);

            vh.recyclerView.setLayoutManager(layoutManager);
            vh.recyclerView.setItemAnimator(new DefaultItemAnimator());

            // Initialize top adapter
            topAdapter = new TopAdapter(MainActivity.this, topModels);
            vh.recyclerView.setAdapter(topAdapter);
        }

    });}
}