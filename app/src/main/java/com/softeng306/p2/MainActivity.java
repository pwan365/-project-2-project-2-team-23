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
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.ViewModel.TopModel;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.List;

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

    IVehicleDataAccess vda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VehicleService.getInstance().InjectService(this);

        List<Integer> is = new ArrayList<>();
        is.add(101);
        is.add(301);
        is.add(201);

        vda.getVehicleById(is, vehicleList -> {
            for (Vehicle v: vehicleList){
                System.out.println(v.getImageNames());
            }
        });

        // Initialise views for future references
        ViewHolder vh = new ViewHolder();
        vh.SearchBar = findViewById(R.id.SearchBar);
        vh.CatElectric = findViewById(R.id.Electric);
        vh.CatHybrid = findViewById(R.id.Hybrid);
        vh.CatPetrol = findViewById(R.id.Petrol);
        vh.recyclerView = findViewById(R.id.recycler_view);
        vh.bottomNavigationView = findViewById(R.id.nav_bar);

        // Create integer array

        // Create string array
        String[] topName = {"Taycan","Mach-E","Combi","Xpeng P5","C-HR","RAV4","Roadster","Model X","Model S","Model 3","Model Y","Model X","Cybertruck"};

        // Initialize arraylist
        topModels =  new ArrayList<>();
        for(int i = 0; i<topName.length;i++){
            TopModel model = new TopModel(topName[i]);
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

    // Open search activity with results of the phrase inputted by user
    public void SearchEventHandler(String phrase) {
        Log.i("MainActivity", "Searching for " + phrase);
        /* TO DO */
    }

    // Open list activity based on the category clicked on
    public void CategoryEventHandler(View v) {
        CardView category = (CardView) v;
        Log.i("MainActivity", "Opening " + category.getContentDescription());
        Intent listIntent = new Intent(this, ListActivity.class);
        listIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Bundle extras = new Bundle();
        int intName= category.getId();
        extras.putString("category", getResources().getResourceEntryName(intName));
        extras.putString("categorySubtitle", (String) category.getContentDescription());
        extras.putParcelable("categoryColour", category.getCardBackgroundColor());
        listIntent.putExtras(extras);
        startActivity(listIntent);
    }

    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }
}