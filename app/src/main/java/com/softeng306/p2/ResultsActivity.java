package com.softeng306.p2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.DataModel.Vehicle;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;

import java.util.ArrayList;
import java.util.List;

/**
 * The ResultsActivity shows all vehicles listed from database relating to the search inputted
 * by a user
 */
public class ResultsActivity extends AppCompatActivity implements CoreActivity {
    private IVehicleDataAccess _vda;
    private String searchPhrase;
    private ArrayList<String> tags;
    private VehicleAdapter vehicleAdapter;
    private RecyclerView recyclerView;
    private LinearLayout noResultsContainer;
    private CardView loadingAnimation;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState Bundle object that gives ability to restore previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Set up connection to the database
        VehicleService.getInstance();
        VehicleService.InjectService(this);

        // Initialise any global array variables
        tags = new ArrayList<>();

        // Find id references to elements in the layout
        recyclerView = findViewById(R.id.results_recycler);
        noResultsContainer = findViewById(R.id.resultsNoResults);
        loadingAnimation = findViewById(R.id.results_load);

        fetchIntent();
        fetchVehicleData();
        initBackBtn();
        initNav();
    }

    /**
     * Method allows activity to receive data from intent and stores as global variables
     */
    private void fetchIntent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        searchPhrase = extras.getString("searchPhrase");
        tags = extras.getStringArrayList("tags");
        setTitle();
    }

    /**
     * Method takes the phrase queried by the user and display it on the activity to confirm
     * correct search
     */
    private void setTitle() {
        TextView phraseTitle = findViewById(R.id.phraseText);
        String phraseText = "\"" + searchPhrase + "\"";
        phraseTitle.setText(phraseText);
    }

    /**
     * Method initialises back button to return to previous activity
     */
    private void initBackBtn() {
        ImageButton backButton = findViewById(R.id.ResultsBackButton);
        backButton.setOnClickListener(v -> {
            // Ensures when results is opened again, that this is not still displayed
            noResultsContainer.setVisibility(View.GONE);
            // Ends the resultsActivity
            finish();
            // Animates the transition to slide down
            overridePendingTransition(R.anim.no_movement, R.anim.slide_to_bottom);
        });
    }

    /**
     * Method sets up the bottom navigation bar to lead to their respective activities
     */
    private void initNav(){
        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
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
            } else if (id == R.id.favourtiesIcon) {
                    Intent favIntent = new Intent(this, FavouritesActivity.class);
                    favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(favIntent);
            }
            return false;
        });
    }

    /**
     * Method retrieves the vehicles by the specific tags and/or vehicle name and passes to the
     * method to propagate the recyclerview of vehicles
     */
    private void fetchVehicleData() {
        if(tags == null || tags.isEmpty()){
            // If no tags were chosen, only search by the vehicle name that matches the search phrase
            _vda.getVehicleByName(searchPhrase, this::propagateListAdaptor);
        } else {
            // If tags have been chosen, get all vehicles matching tag and the refine by filtering
            // with the search phrase
            _vda.getVehicleByTagName(tags, "All", vehicleList -> {
                propagateListAdaptor(vehicleList);
                ResultsActivity.this.vehicleAdapter.getSearchFilter().filter(searchPhrase);
            });
        }
    }

    /**
     * Method propagates the recyclerview with the vehicles from the vehicleList
     * @param vehicleList List of vehicles
     */
    private void propagateListAdaptor(List<Vehicle> vehicleList) {
        // Check if the list is empty, if so, display the no results message
        if(vehicleList.isEmpty()){
            loadingAnimation.setVisibility(View.GONE); // Ensures it displays no results without loading
            noResultsContainer.setVisibility(View.VISIBLE);
        } else {
            initLoading();
            noResultsContainer.setVisibility(View.GONE);
        }

        // Loop through all vehicles to retrieve and store names and prices
        List<String> vehicleName = new ArrayList<>();
        List<Float> vehiclePrice = new ArrayList<>();
        for(Vehicle vehicle : vehicleList) {
            vehicleName.add(vehicle.getVehicleName());
            vehiclePrice.add(vehicle.getPrice());
        }

        // Develop an arraylist of vehicles in their model form to be used in the adapter
        ArrayList<Vehicle> vehicleModels = new ArrayList<>();
        for(int i = 0; i<vehicleList.size();i++){
            Vehicle model = new Vehicle(vehicleName.get(i), vehiclePrice.get(i));
            vehicleModels.add(model);
        }

        // Format recyclerview in a grid layout with two columns if portrait and horizontal layout if landscape
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        // Set up the vehicle adapter to finally display the vehicles in the recyclerview
        vehicleAdapter = new VehicleAdapter(ResultsActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);

    }

    /**
     * Method ensures the loading animation is only run for a certain duration before showing
     * the results list
     */
    private void initLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        // Delays the removal of the loading animation
        loadingAnimation.postDelayed(new Runnable() {
            public void run() {
                loadingAnimation.animate()
                        // Progress circle is animated to fade down
                        .translationY(loadingAnimation.getHeight())
                        .alpha(0.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Removes the progress circle and shows the results recyclerview
                                super.onAnimationEnd(animation);
                                loadingAnimation.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 1000); // time given to load for
    }

    /**
     * Set up the database access for this activity
     * @param vehicleDataAccess Interface that provides access to the database
     */
    @Override
    public void setDataAccess(IVehicleDataAccess vehicleDataAccess) {
        _vda = vehicleDataAccess;
    }
}
