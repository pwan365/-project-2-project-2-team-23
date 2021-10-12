package com.softeng306.p2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.Database.VehicleDataAccess;
import com.softeng306.p2.Model.TagModel;
import com.softeng306.p2.Models.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<Integer> recyclerIds;
    private List<TagAdapter> adapters;
    private LinearLayout tagContainer;
    private ColorStateList CatColourState;
    private SearchView SearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //init arrays
        recyclerIds = new ArrayList<>();
        adapters = new ArrayList<>();

        CatColourState = ColorStateList.valueOf(getResources().getColor(R.color.yellow));
        initRefineBtns();

        // Set up the search bar
        SearchBar = findViewById(R.id.SearchBar);
        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                ArrayList<String> onTags = GetOnTags();
                SearchEventHandler(searchInput, onTags);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });

        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    Intent i1 = new Intent(this, MainActivity.class);
                    i1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i1);
                    break;
                case R.id.searchIcon:
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

    private ArrayList<String> GetOnTags() {
        ArrayList<String> onTags = new ArrayList<>();
        for (TagAdapter a : adapters) {
            for (String s : a.getOnTags()) {
                onTags.add(s);
            }
        }
        return onTags;
    }

    private void initRefineBtns() {
        tagContainer = findViewById(R.id.SearchTagContainer);
        VehicleDataAccess vda = new VehicleDataAccess();
        vda.getAllTags(tagList -> {
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
}
