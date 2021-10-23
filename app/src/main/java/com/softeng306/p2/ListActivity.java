package com.softeng306.p2;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.Adapter.VehicleAdapter;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Helpers.SortTagsByType;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.DataModel.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The ListActivity displays a list of vehicles pertaining to a category
 */
public class ListActivity extends AppCompatActivity implements CoreActivity {

    private IVehicleDataAccess _vda;
    private SearchView searchBar;
    private ImageView closeSearch;
    private ImageButton searchButton;
    private String categoryName, categorySubtitle;
    private View bottomSheetView;
    private List<TagAdapter> adapters;
    private BottomSheetDialog dialog;
    private int catColourInt;
    private ColorStateList catColourState;
    private VehicleAdapter vehicleAdapter;
    private LinearLayout noResults, tagsContainer;
    RecyclerView recyclerView;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState Bundle object that gives ability to restore previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Set up connection to the database
        VehicleService.getInstance();
        VehicleService.InjectService(this);

        // Find id references to elements in the layout
        recyclerView = findViewById(R.id.listRecycler);
        searchButton = findViewById(R.id.listSearchButton);
        searchBar = findViewById(R.id.listSearchBar);
        closeSearch = findViewById(R.id.closeSearchArea);
        noResults = findViewById(R.id.listNoResults);

        // Initialise any global array variables
        adapters = new ArrayList<>();

        fetchIntent();
        fetchVehicleData();
        initRefineDialog();
        initHeaderStyling();
        setUpRefineBtn();
        searchSetup();
        initNav();
        setUpNoResults();
    }

    /**
     * Method allows activity to receive data from intent and stores as global variables
     */
    private void fetchIntent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        categoryName = extras.getString("category");
        categorySubtitle = extras.getString("categorySubtitle");
        catColourState = extras.getParcelable("categoryColour");
        catColourInt = catColourState.getDefaultColor();
    }

    /**
     * Method initialises the refine button
     */
    private void setUpRefineBtn() {
        // Set the refine colour
        RelativeLayout refineBtn = findViewById(R.id.refineBtn);
        refineBtn.setBackgroundTintList(catColourState);

        // Initialise refine button
        refineBtn.setOnClickListener(v -> dialog.show());
    }

    /**
     * Method sets up all the styling for the header section of the list activity
     */
    private void initHeaderStyling() {
        // Set the list title
        TextView catTitle = findViewById(R.id.listTitle);
        catTitle.setText(categoryName);

        // Set the list subtitle
        TextView catSubtitle = findViewById(R.id.listSubtitle);
        catSubtitle.setText(categorySubtitle);

        // Set the list image
        ImageView catImg = findViewById(R.id.listHeaderImage);
        int resId = getResources().getIdentifier(categoryName.toLowerCase(Locale.ROOT), "drawable", ListActivity.this.getPackageName());
        Drawable d = ResourcesCompat.getDrawable(getResources(), resId, null);
        catImg.setImageDrawable(d);

        // Set the heading colour
        RelativeLayout listHeading = findViewById(R.id.listHeader);
        RelativeLayout listActionBar = findViewById(R.id.listActionBar);
        listHeading.setBackgroundColor(catColourInt);
        listActionBar.setBackgroundColor(catColourInt);

        // Initialize back button
        ImageButton listBackButton = findViewById(R.id.listBackButton);
        listBackButton.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.no_movement, R.anim.slide_to_bottom);
        });
    }

    /**
     * Method sets up the bottom navigation bar to lead to their respective activities
     */
    public void initNav() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar); // Find the nav view

        // Makes the search icon in the second position highlighted yellow
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        // Initialise the navigation buttons
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            int id = item.getItemId();
            if (id == R.id.homeIcon) {
                finish();
                overridePendingTransition(0, R.anim.slide_to_bottom);
            } else if (id == R.id.searchIcon) {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                overridePendingTransition(0, R.anim.slide_to_bottom);
            } else if (id == R.id.favourtiesIcon) {
                Intent favIntent = new Intent(this, FavouritesActivity.class);
                startActivity(favIntent);
                overridePendingTransition(0, R.anim.slide_to_bottom);
            }
            return false;
        });
    }

    /**
     * Method adds click function to search icon button, to display the search bar
     */
    public void searchSetup() {
        searchButton.setOnClickListener(v -> {
            // Display a empty container that provides the user to click anywhere
            // to close the search bar
            closeSearch.setVisibility(View.VISIBLE);
            closeSearch.bringToFront();
            // Show the search bar
            searchBar.setVisibility(View.VISIBLE);
            searchBar.requestFocus();
            searchBar.bringToFront();
            // Displays the keyboard and focuses on the search bar to start typing instantly
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);

        });

        // Clicking on the closeSearch container will hide the search bar and itself
        closeSearch.setOnClickListener(v -> {
            searchBar.setVisibility(View.INVISIBLE);
            closeSearch.setVisibility(View.INVISIBLE);
        });

        // Listener for search bar input from user
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                // When user submits their query, filters the vehicle adapter to show related results
                vehicleAdapter.getSearchFilter().filter(searchInput);
                // Hide the search bar and close search container
                searchBar.setVisibility(View.INVISIBLE);
                closeSearch.setVisibility(View.INVISIBLE);
                // Checks if any vehicles are returned after search
                if (vehicleAdapter.getItemCount() == 0) {
                    // Displays the no search message to user if no vehicles returned
                    noResults.setVisibility(View.VISIBLE);
                } else {
                    // If returns vehicles, hide the no results message
                    noResults.setVisibility(View.GONE);
                }
                return false;
            }

            // As the user types in the search bar, filters the displayed vehicle list
            @Override
            public boolean onQueryTextChange(String searchInput) {
                if(vehicleAdapter != null) {
                    vehicleAdapter.getSearchFilter().filter(searchInput);
                }
                return false;
            }
        });
    }

    /**
     * Method sets up the reset button in the no results container
     */
    private void setUpNoResults() {
        // Change colour of reset button to match category theme colour
        Button listResetBtn = findViewById(R.id.listResetBtn);
        listResetBtn.setBackgroundTintList(catColourState);

        // Clicking on the reset button to redisplay the original vehicle list and refine dialog
        listResetBtn.setOnClickListener(view -> {
            fetchVehicleData();
            initRefineDialog();
        });
    }

    /**
     * Method retrieves the vehicles from the specific category and passes to the method to
     * propagate the recyclerview of vehicles
     */
    private void fetchVehicleData() {
        _vda.getCategoryVehicles(categoryName, this::propagateListAdaptor);
    }

    /**
     * Method propagates the recyclerview with the vehicles from the vehicleList
     * @param vehicleList List of vehicles from the category
     */
    private void propagateListAdaptor(List<Vehicle> vehicleList) {

        // Check if the list is empty, if so, display the no results message
        if (vehicleList.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.GONE);
        }

        // Loop through all vehicles to retrieve and store names and prices
        List<String> vehicleName = new ArrayList<>();
        List<Float> vehiclePrice = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            vehicleName.add(vehicle.getVehicleName());
            vehiclePrice.add(vehicle.getPrice());
        }

        // Develop an arraylist of vehicles in their model form to be used in the adapter
        ArrayList<Vehicle> vehicleModels = new ArrayList<>();
        for (int i = 0; i < vehicleList.size(); i++) {
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

        // Set up the vehicle adapter to finally display the vehicles in the recyclerview
        vehicleAdapter = new VehicleAdapter(ListActivity.this, vehicleModels);
        recyclerView.setAdapter(vehicleAdapter);
    }

    /**
     * Method sets up the refine dialog that pops open when the refine button is clicked.
     */
    private void initRefineDialog() {

        // Initialise a bottom sheet dialog with the layout of the activity_refine XML file
        dialog = new BottomSheetDialog(ListActivity.this, R.style.BottomSheetTheme);
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.activity_refine, findViewById(R.id.bottomSheetContainer));
        tagsContainer = bottomSheetView.findViewById(R.id.tagsContainer);
        dialog.setContentView(bottomSheetView);

        // Set up function behind the submit button in the refine dialog
        refineSubmit();

        // Ensures the refine dialog opens entirely to display all content on the bottom sheet dialog
        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Get all tags from the database and displays them in the refine log
        _vda.getAllTags(this::displayTags);

    }

    /**
     * Method takes a list of tags, sort them by type and displays them on the refine dialog
     * @param tagList List of Tag objects
     */
    private void displayTags(List<Tag> tagList) {
        // Sorts tags by their tag type
        ArrayList<List<String>> sortedTags = SortTagsByType.listTagTypes(tagList);

        // Loop through each tag types list to display the tag type as text and to display its'
        // related tags in a recyclerview
        for (List<String> typeTagList : sortedTags) {
            // First position of the list stores the tag type name
            String type = typeTagList.get(0);

            // Ignores the vehicle type as the category already refines by this type
            if (!"VEHICLE TYPE".equals(type)) {
                // Inserts the title of this type as a textview
                TextView typeTitle = new TextView(ListActivity.this);
                typeTitle.setText(type);
                typeTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tagsContainer.addView(typeTitle,2);
                // Insert a recyclerview of tags
                propagateTagAdaptor(typeTagList);
            }

        }
    }

    /**
     * Method sets styling and functionality of submit button in the refine dialog
     */
    private void refineSubmit() {
        // Finds the button and matched the styling to the categories colour
        Button submitRefineBtn = bottomSheetView.findViewById(R.id.submitRefineBtn);
        submitRefineBtn.setBackgroundTintList(catColourState);

        // Clicking on 'apply filters' will refine the current vehicle list in the listActivity
        submitRefineBtn.setOnClickListener(view -> {
            // Finds and lists all the tags the user has clicked
            List<String> onTags = new ArrayList<>();
            for (TagAdapter a : adapters) {
                onTags.addAll(a.getOnTags());
            }

            // Checking if at least one tag has been chosen
            if (onTags.isEmpty()) {
                // If no tags have been chosen (empty list), will provide a toast to inform the user
                Toast.makeText(getApplicationContext(), "The list has been reset",
                        Toast.LENGTH_SHORT).show();
                fetchVehicleData();
            } else {
                // Propagates the vehicle list again with all vehicles found by category and tags selected
                fetchVehicleData();
                _vda.getVehicleByTagName(onTags, categoryName, ListActivity.this::propagateListAdaptor);
                dialog.hide();
            }
        });
    }

    /**
     * Method propagates a recyclerview with tags from the tagNames list
     * @param tagNames List of tags by their tag name
     */
    private void propagateTagAdaptor(List<String> tagNames) {

        // Creates a new recyclerview for this list of tags along with an id for it
        RecyclerView tagRecyclerView = new RecyclerView(ListActivity.this);
        int id = View.generateViewId();
        tagRecyclerView.setId(id);

        // Develop an arraylist of tags in their model form to be used in the adapter
        ArrayList<Tag> tagModels = new ArrayList<>();
        for (int i = 1; i < tagNames.size(); i++) {
            Tag model = new Tag(tagNames.get(i));
            tagModels.add(model);
        }

        // Display recyclerview in a horizontal layout
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, GridLayoutManager.HORIZONTAL, false));

        // Set up the tag adapter to finally display the vehicles in the recyclerview
        TagAdapter tagAdapter = new TagAdapter(ListActivity.this, tagModels, catColourState, false);
        adapters.add(tagAdapter);
        tagRecyclerView.setAdapter(tagAdapter);

        // Adds this new recyclerview in the 3rd position which is below the refine title, bar and type title
        tagsContainer.addView(tagRecyclerView,3);
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