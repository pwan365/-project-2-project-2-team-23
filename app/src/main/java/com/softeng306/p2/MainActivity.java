package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.TopAdapter;
import com.softeng306.p2.Database.VehicleDataAccess;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.Model.TopModel;
import com.softeng306.p2.Models.Tag;
import com.softeng306.p2.Models.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        VehicleDataAccess vda = new VehicleDataAccess();
        List<Integer> is = new ArrayList<>();
        is.add(101);
        is.add(301);
        is.add(201);

        vda.getVehicleById(is, new OnGetVehicleListener() {
            @Override
            public void onCallBack(List<Vehicle> vehicleList) {
                for (Vehicle v: vehicleList){
                    System.out.println(v.getImageNames());
                }
            }
        });





        //assign variable
        recyclerView = findViewById(R.id.recycler_view);
        // Initialise views for future references
        ViewHolder vh = new ViewHolder();
        vh.SearchBar = (SearchView) findViewById(R.id.SearchBar);
        vh.CatElectric = (CardView) findViewById(R.id.CatElectric);
        vh.CatHybrid = (CardView) findViewById(R.id.CatHybrid);
        vh.CatPetrol = (CardView) findViewById(R.id.CatPetrol);
        vh.recyclerView = findViewById(R.id.recycler_view);
        vh.bottomNavigationView = findViewById(R.id.nav_bar);

        // Create integer array
        Integer[] topImg = {R.drawable.hatchback,R.drawable.sedan,R.drawable.pickup_truck,R.drawable.pickup_truck,R.drawable.pickup_truck,R.drawable.pickup_truck};

        // Create string array
        String[] topName = {"Model 3","Model S","Roadster","Model Y","Model X","Cyber Truck"};

        // Initialize arraylist
        topModels =  new ArrayList<>();
        for(int i = 0; i<topImg.length;i++){
            TopModel model = new TopModel(topImg[i],topName[i]);
            topModels.add(model);
        }

        // Design Horizontal Layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false);

        vh.recyclerView.setLayoutManager(layoutManager);
        vh.recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize top adapter
        topAdapter = new TopAdapter(MainActivity.this,topModels);
        vh.recyclerView.setAdapter(topAdapter);

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
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(searchIntent);
                    break;
                case R.id.favourtiesIcon:
                    Intent favIntent = new Intent(MainActivity.this, FavouritesActivity.class);
                    favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(favIntent);
                    break;
            }
        return false;
        });
    }


    // Open search activity with results of the phrase inputted by user
    public void SearchEventHandler(String phrase) {
        Log.i("MainActivity", "Searching for " + phrase);
        /* TO DO */
    }

    // Open list activity based on the category clicked on
    public void CategoryEventHandler(View v) {
        CardView category = (CardView) v;
        Log.i("MainActivity", "Opening " + category.getContentDescription());
        /* TO DO */
    }
}