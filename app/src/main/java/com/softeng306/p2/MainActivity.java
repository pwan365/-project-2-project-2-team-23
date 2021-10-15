package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Helpers.VehicleComparator;
import com.softeng306.p2.ViewModel.TopModel;
import com.softeng306.p2.DataModel.Vehicle;
import com.softeng306.p2.ViewModel.VehicleModel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToLongBiFunction;

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

    private void fetchTopPickData(){vda.getFavourites(this::propagateUsersAdaptor);}

    private void propagateUsersAdaptor(User user) {
        vda.getVehicleById(user.getFavourites(), this::propagateFavouritesAdaptor);
    }

    private void propagateFavouritesAdaptor(List<Vehicle> vehicleList) {
        // Create string array
        String[] topName = {"Taycan","Mach-E","Combi","Xpeng P5","C-HR","RAV4","Roadster","Model X","Model S","Model 3","Model Y","Cybertruck"};
        List<String> defaultTopList = Arrays.asList(topName);
        Collections.shuffle(defaultTopList);
        Collections.shuffle(vehicleList);
        // Initialize arraylist
        topModels =  new ArrayList<>();
        int topPickFav = 3;
        for(int i = 0;i <vehicleList.size();i++){
            if(i<topPickFav){
                if(!defaultTopList.contains(vehicleList.get(i).getVehicleName())){
                    TopModel model = new TopModel(vehicleList.get(i).getVehicleName());
                    topModels.add(model);
                }

            }
        }
        for(String vehicleName: defaultTopList){
            TopModel model = new TopModel(vehicleName);
            topModels.add(model);
        }



        // Design Horizontal Layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false);

        vh.recyclerView.setLayoutManager(layoutManager);
        vh.recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize top adapter
        topAdapter = new TopAdapter(MainActivity.this,topModels);
        vh.recyclerView.setAdapter(topAdapter);
    }
}