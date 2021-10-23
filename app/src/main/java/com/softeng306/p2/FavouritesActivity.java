package com.softeng306.p2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * The FavouritesActivity displays a list of vehicles that is favourited by the user
 */
public class FavouritesActivity extends AppCompatActivity implements CoreActivity {

    private IVehicleDataAccess _vda;
    private RecyclerView favourites_recycler;
    private CardView loadingCard;
    private LinearLayout noResultsContainer;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState Bundle object that gives ability to restore previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //Initialize database access
        VehicleService.getInstance();
        VehicleService.InjectService(this);

        //Find recycler
        favourites_recycler = findViewById(R.id.favourites_recycler);


        noResultsContainer = findViewById(R.id.favsNoResults);
        loadingCard = findViewById(R.id.favourites_load);

        fetchVehicleData();
        initLoading();
        initNavigation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchVehicleData();
        initLoading();
    }

    /**
     * Initialize the bottom navigation buttons
     */
    public void initNavigation() {
        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            int id = item.getItemId();
            if (id == R.id.homeIcon) {
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(homeIntent);
            } else if (id == R.id.searchIcon) {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(searchIntent);
            }
            return false;
        });
    }

    /**
     * initialize favourites activity with loading animation
     */
    private void initLoading() {
        CardView cardView = findViewById(R.id.favourites_load);
        favourites_recycler.setVisibility(View.INVISIBLE);
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
                                favourites_recycler.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 1000);
    }

    /**
     * setup the vehicle data access of this activity
     * @param vehicleDataAccess
     */
    @Override
    public void setDataAccess(IVehicleDataAccess vehicleDataAccess) {
        this._vda = vehicleDataAccess;
    }

    /**
     * retrieve the vehicles from the user's favourite list in firebase
     */
    private void fetchVehicleData() {
        _vda.getFavourites(this::propagateUsersAdaptor);
    }

    /**
     * Method to propagate the favourites adaptor with the User object
     * @param user
     */
    private void propagateUsersAdaptor(User user) {
        _vda.getVehicleById(user.getFavourites(), this::propagateFavouritesAdaptor);
    }

    /**
     * Method propagates a recyclerview with favourites from the favourite's list
     * @param vehicleList
     */
    private void propagateFavouritesAdaptor(List<Vehicle> vehicleList) {
        RecyclerView recyclerView = findViewById(R.id.favourites_recycler);

        if(vehicleList.isEmpty()){
            loadingCard.setVisibility(View.GONE);
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
        ArrayList<Vehicle> vehicleModels = new ArrayList<>();
        for(int i = 0; i<vehicleList.size();i++){
            Vehicle model = new Vehicle(vehicleName.get(i), vehiclePrice.get(i));
            vehicleModels.add(model);
        }

        // Format recyclerview in a grid layout with two columns if portrait and horizontal layout
        // if device is in landscape mode
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }


        // Initialize top adapter
        VehicleAdapter vehicleAdapter = new VehicleAdapter(FavouritesActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);

    }

}