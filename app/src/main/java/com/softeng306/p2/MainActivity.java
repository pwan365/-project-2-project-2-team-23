package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.Vehicle;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CoreActivity {

     class ViewHolder {
        private CardView catElectric, catHybrid, catPetrol, loadingView;
        private SearchView searchBar;
        private RecyclerView recyclerView;
        private BottomNavigationView bottomNavigationView;
        private LinearLayout topPickContainer, catContainer;

        public ViewHolder(){
            searchBar = findViewById(R.id.SearchBar);
            catElectric = findViewById(R.id.Electric);
            catHybrid = findViewById(R.id.Hybrid);
            catPetrol = findViewById(R.id.Petrol);
            recyclerView = findViewById(R.id.recycler_view);
            bottomNavigationView = findViewById(R.id.nav_bar);

            //elements for loading
            loadingView = findViewById(R.id.main_load);
            topPickContainer = findViewById(R.id.TopPicksContainer);
            catContainer = findViewById(R.id.catContainer);
        }

    }

    //Initialize fields used in multiple methods
    ArrayList<VehicleModel> topModels;
    TopAdapter topAdapter;
    ViewHolder vh;
    IVehicleDataAccess vda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //injection database service
        VehicleService.getInstance().InjectService(this);

        // Initialise views for future references
        vh = new ViewHolder();

        //construct the view based on the data
        fetchTopPickData();
        initLoading();
        initNav();
        initSearch();
        initCatBtns();
    }

    /**
     * initialize category buttons with event handler
     */
    private void initCatBtns() {
        // Initialise the category buttons
        vh.catElectric.setOnClickListener(this::CategoryEventHandler);
        vh.catHybrid.setOnClickListener(this::CategoryEventHandler);
        vh.catPetrol.setOnClickListener(this::CategoryEventHandler);
    }

    /**
     * initialize the search bar at the top of main activity
     */
    private void initSearch() {
        // Set up the search bar
        vh.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                SearchEventHandler(searchInput);
                vh.searchBar.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });

        View bg = findViewById(R.id.mainBodyContainer);
        bg.setOnClickListener(view -> {
            vh.searchBar.clearFocus();
        });
    }

    /**
     * initialize the bottom navigation bar
     */
    private void initNav() {
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

    /**
     * initialize main activity with loading animation
     */
    private void initLoading() {
        vh.loadingView = findViewById(R.id.main_load);
        vh.topPickContainer = findViewById(R.id.TopPicksContainer);
        vh.catContainer = findViewById(R.id.catContainer);
        vh.topPickContainer.setVisibility(View.INVISIBLE);
        vh.catContainer.setVisibility(View.INVISIBLE);

        //set duration for the loading animation
        vh.loadingView.postDelayed(new Runnable() {
            public void run() {
                vh.loadingView.animate()
                        .translationY(vh.loadingView.getHeight())
                        .alpha(0.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                vh.loadingView.setVisibility(View.GONE);
                                vh.topPickContainer.setVisibility(View.VISIBLE);
                                vh.catContainer.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 1500);
    }


    /**
     * Open search activity with results of the phrase inputted by user
     * @param phrase
     */
    public void SearchEventHandler(String phrase) {
        Intent listIntent = new Intent(this, ResultsActivity.class);
        listIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        listIntent.putExtra("searchPhrase", phrase);
        startActivity(listIntent);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.no_movement);
    }

    // Open list activity based on the category clicked on
    public void CategoryEventHandler(View v) {
        CardView category = (CardView) v;
        Log.i("MainActivity", "Opening " + category.getContentDescription());
        Intent listIntent = new Intent(this, ListActivity.class);

        Bundle extras = new Bundle();
        int intName= category.getId();
        extras.putString("category", getResources().getResourceEntryName(intName));
        extras.putString("categorySubtitle", (String) category.getContentDescription());
        extras.putParcelable("categoryColour", category.getCardBackgroundColor());
        listIntent.putExtras(extras);
        startActivity(listIntent);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.no_movement);
    }

    /**
     * initialize the database object
     * @param vehicleDataAccess
     */
    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }

    /**
     * using a advanced method to fetch top pick every time main activity loads
     */
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
                VehicleModel model = new VehicleModel(vehicle.getVehicleName(),vehicle.getPrice());
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