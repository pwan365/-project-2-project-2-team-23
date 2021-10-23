package com.softeng306.p2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Helpers.SortTagsByType;

import java.util.ArrayList;
import java.util.List;

/**
 * The SearchActivity allows the user to search by string input and tags to find vehicles
 * from the database
 */
public class SearchActivity extends AppCompatActivity implements CoreActivity {
    private IVehicleDataAccess _vda;
    private List<TagAdapter> adapters;
    private LinearLayout tagContainer;
    private ColorStateList catColourState;
    private SearchView searchBar;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState Bundle object that gives ability to restore previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up connection to the database
        VehicleService.getInstance();
        VehicleService.InjectService(this);

        // Initialise any global array variables
        adapters = new ArrayList<>();
        // Find id references to elements in the layout
        catColourState = ColorStateList.valueOf(getResources().getColor(R.color.yellow));

        showTags();
        initLoading();
        initSearch();
        initNav();
    }

    /**
     * Method adds click function to search bar which submitting leads to the resultsActivity
     * with the search query (and tags) inputted by user
     */
    private void initSearch() {
        // Set up the search bar
        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                // Passes through the tags chosen by the user
                ArrayList<String> onTags = getOnTags();
                searchEventHandler(searchInput, onTags);
                searchBar.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });

        // Clicking on the background container will close keyboard and removes the focus
        View bg = findViewById(R.id.searchBodyContainer);
        bg.setOnClickListener(view -> searchBar.clearFocus());
    }

    /**
     * Method ensures the loading animation is only run for a certain duration before showing
     * the tags
     */
    private void initLoading() {
        // Hides the tags as they load in from database
        CardView cardView = findViewById(R.id.searchLoad);
        tagContainer.setVisibility(View.INVISIBLE);
        // Delays the removal of the loading animation
        cardView.postDelayed(new Runnable() {
            public void run() {
                cardView.animate()
                        // Progress circle is animated to fade down
                        .translationY(cardView.getHeight())
                        .alpha(0.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Removes the progress circle and shows the tags in their recyclerview
                                super.onAnimationEnd(animation);
                                cardView.setVisibility(View.GONE);
                                tagContainer.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 1000); // time given to load for
    }

    /**
     * Sets up the bottom navigation bar to lead to their respective activities
     */
    private void initNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar); // Find the nav view

        // Makes the search icon in the second position highlighted yellow
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        // Initialise the navigation buttons
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            int id = item.getItemId();
            if (id == R.id.homeIcon) {
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mainIntent);
            } else if ( id == R.id.favourtiesIcon) {
                Intent favIntent = new Intent(this, FavouritesActivity.class);
                favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(favIntent);
            }
            return false;
        });
    }

    /**
     * Method returns the tags toggled on in the tag adapters
     * @return a list of the tags toggled on by the user to use in their search
     */
    private ArrayList<String> getOnTags() {
        ArrayList<String> onTags = new ArrayList<>();
        // Loop through each tag types adapter to add all the tags that are toggled on
        for (TagAdapter a : adapters) {
            onTags.addAll(a.getOnTags());
        }
        return onTags;
    }

    /**
     * Method retrieves all tags from the database and display in their respective recyclerview
     */
    private void showTags() {
        // Find the container pre-made in the XML for the tags
        tagContainer = findViewById(R.id.searchTagContainer);

        // Retrieve tags from database
        _vda.getAllTags(tagList -> {
            // Sorts tags by their tag type
            ArrayList<List<String>> sortedTags = SortTagsByType.listTagTypes(tagList);

            // Loop through each tag types list to display the tag type as text and to display its'
            // related tags in a recyclerview
            for(List<String> typeTagList : sortedTags) {
                String type = typeTagList.get(0);
                TextView typeTitle = new TextView(SearchActivity.this);
                typeTitle.setText(type);
                typeTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tagContainer.addView(typeTitle);

                propagateTagAdaptor(typeTagList);

            }
        });

    }

    /**
     * Method propagates a recyclerview with tags from the tagNames list
     * @param tagNames List of tags by their tag name
     */
    private void propagateTagAdaptor(List<String> tagNames) {
        // Creates a new recyclerview for this list of tags along with an id for it
        RecyclerView tagRecyclerView = new RecyclerView(SearchActivity.this);
        int id = View.generateViewId();
        tagRecyclerView.setId(id);

        // Develop an arraylist of tags in their model form to be used in the adapter
        ArrayList<Tag> tagModels = new ArrayList<>();
        for(int i = 1; i<tagNames.size();i++){
            Tag model = new Tag(tagNames.get(i));
            tagModels.add(model);
        }

        // Display recyclerview in a horizontal layout
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, GridLayoutManager.HORIZONTAL, false));

        // Set up the tag adapter to finally display the vehicles in the recyclerview
        TagAdapter tagAdapter = new TagAdapter(SearchActivity.this, tagModels,
                catColourState, false);
        adapters.add(tagAdapter);
        tagRecyclerView.setAdapter(tagAdapter);

        // Adds the new recyclerview to the container preset in XML layout
        tagContainer.addView(tagRecyclerView);
    }

    /**
     * Start ResultsActivity and passes through the phrase and tags inputted by user
     *
     * @param phrase string query from user
     * @param onTags list of tags that the user selected
     */
    public void searchEventHandler(String phrase, ArrayList<String> onTags) {
        // Bundles the phrase and tags to be passed through an intent and led to resultsActivity
        Intent listIntent = new Intent(this, ResultsActivity.class);
        Bundle b = new Bundle();
        b.putString("searchPhrase", phrase);
        b.putStringArrayList("tags", onTags);
        listIntent.putExtras(b);
        startActivity(listIntent);

        // Animates the transition between the two activities
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.no_movement);
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
