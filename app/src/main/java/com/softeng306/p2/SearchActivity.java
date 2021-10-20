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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.DataModel.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements CoreActivity {
    private IVehicleDataAccess _vda;

    private List<Integer> recyclerIds;
    private List<TagAdapter> adapters;
    private LinearLayout tagContainer;
    private ColorStateList CatColourState;
    private SearchView SearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        VehicleService.getInstance().InjectService(this);

        //init arrays
        recyclerIds = new ArrayList<>();
        adapters = new ArrayList<>();

        CatColourState = ColorStateList.valueOf(getResources().getColor(R.color.yellow));
        showTags();

        CardView cardView = findViewById(R.id.search_load);
        tagContainer.setVisibility(View.INVISIBLE);
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
                                tagContainer.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }, 1000);

        // Set up the search bar
        SearchBar = findViewById(R.id.SearchBar);
        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                ArrayList<String> onTags = GetOnTags();
                SearchEventHandler(searchInput, onTags);
                SearchBar.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });

        View bg = findViewById(R.id.searchBodyContainer);
        bg.setOnClickListener(view -> {
            SearchBar.clearFocus();
        });

        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    Intent mainItent = new Intent(this, MainActivity.class);
                    mainItent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mainItent);
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                    break;
                case R.id.searchIcon:
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

    private ArrayList<String> GetOnTags() {
        ArrayList<String> onTags = new ArrayList<>();
        for (TagAdapter a : adapters) {
            for (String s : a.getOnTags()) {
                onTags.add(s);
            }
        }
        return onTags;
    }

    private void showTags() {
        tagContainer = findViewById(R.id.SearchTagContainer);
        _vda.getAllTags(tagList -> {
            ArrayList<List<String>> sortedTags = listTagTypes(tagList);

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

    private ArrayList<List<String>> listTagTypes(List<Tag> tagsList) {

        // Create string hash set of types
        LinkedHashSet<String> tagTypes = new LinkedHashSet<>();
        for(Tag tag : tagsList) {
            tagTypes.add(tag.getTagType());
        }

        ArrayList<List<String>> sortedTags = new ArrayList<>();
        // Sort tags by type
        for(String type: tagTypes) {
            List<String> tagNames = new ArrayList<>();
            tagNames.add(type);
            for (Tag tag : tagsList) {
                if (tag.getTagType().equals(type)) {
                    tagNames.add(tag.getTagName());
                }
            }
            sortedTags.add(tagNames);
        }

        return sortedTags;
    }

    private void propagateTagAdaptor(List<String> tagNames) {
        RecyclerView tagRecyclerView = new RecyclerView(SearchActivity.this);
        int id = View.generateViewId();
        tagRecyclerView.setId(id);
        recyclerIds.add(id);
        TagAdapter tagAdapter;

        // Initialize arraylist
        ArrayList<TagModel> tagModels = new ArrayList<>();
        for(int i = 1; i<tagNames.size();i++){
            TagModel model = new TagModel(tagNames.get(i));
            tagModels.add(model);
        }

        // Design horizontal layout
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize adapter
        tagAdapter = new TagAdapter(SearchActivity.this, tagModels, CatColourState);
        adapters.add(tagAdapter);
        tagRecyclerView.setAdapter(tagAdapter);

        tagContainer.addView(tagRecyclerView);
    }

    // Open search activity with results of the phrase inputted by user
    public void SearchEventHandler(String phrase, ArrayList<String> onTags) {
        Intent listIntent = new Intent(this, ResultsActivity.class);
        listIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Bundle b = new Bundle();
        b.putString("searchPhrase", phrase);
        b.putStringArrayList("tags", onTags);
        listIntent.putExtras(b);
        startActivity(listIntent);
    }

    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        _vda = vehicleDataAccess;
    }
}
