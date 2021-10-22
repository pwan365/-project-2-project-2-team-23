package com.softeng306.p2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity implements CoreActivity {

    private IVehicleDataAccess _vda;
    private RecyclerView favourites_recycler;
    private VehicleAdapter vehicleAdapter;
    private CardView cardView;
    private LinearLayout noResultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //Initialize database access
        VehicleService.getInstance().InjectService(this);

        //Find recycler
        favourites_recycler = findViewById(R.id.favourites_recycler);


        noResultsContainer = findViewById(R.id.favsNoResults);
        cardView = findViewById(R.id.favourites_load);

        fetchVehicleData();
        initLoading();
        initNavigation();
    }

    public void initNavigation() {
        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    Intent homeIntent = new Intent(this, MainActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(homeIntent);
//                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                    break;
                case R.id.searchIcon:
                    Intent searchIntent = new Intent(this, SearchActivity.class);
                    searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(searchIntent);
//                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                    break;
                case R.id.favourtiesIcon:
                    break;
            }
            return false;
        });
    }

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

    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        this._vda = vehicleDataAccess;
    }

    private void fetchVehicleData() {
        _vda.getFavourites(this::propagateUsersAdaptor);
    }

    private void propagateUsersAdaptor(User user) {
        _vda.getVehicleById(user.getFavourites(), this::propagateFavouritesAdaptor);
    }

    private void propagateFavouritesAdaptor(List<Vehicle> vehicleList) {
        RecyclerView recyclerView = findViewById(R.id.favourites_recycler);

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
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Initialize top adapter
        vehicleAdapter = new VehicleAdapter(FavouritesActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);

    }

}