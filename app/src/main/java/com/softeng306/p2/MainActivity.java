package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    private CardView CatElectric;
    private CardView CatHybrid;
    private CardView CatPetrol;
    private SearchView SearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the search bar
        SearchBar = (SearchView) findViewById(R.id.SearchBar);
        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInput) {
                OpenCatList(searchInput);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });

        // Set up the categories as buttons
        CatElectric = (CardView) findViewById(R.id.CatElectric);
        CatElectric.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenCatList("electric");
            }
        });

        CatHybrid = (CardView) findViewById(R.id.CatHybrid);
        CatHybrid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenCatList("hybrid");
            }
        });

        CatPetrol = (CardView) findViewById(R.id.CatPetrol);
        CatPetrol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenCatList("petrol");
            }
        });
    }

    // Open category list activity based on the category clicked on
    public void OpenCatList(String category) {
        Log.i("MainActivity", "Opening " + category);
    }
}