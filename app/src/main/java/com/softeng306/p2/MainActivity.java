package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    class ViewHolder {
        private CardView CatElectric, CatHybrid, CatPetrol;
        private SearchView SearchBar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise views for future references
        ViewHolder vh = new ViewHolder();
        vh.SearchBar = (SearchView) findViewById(R.id.SearchBar);
        vh.CatElectric = (CardView) findViewById(R.id.CatElectric);
        vh.CatHybrid = (CardView) findViewById(R.id.CatHybrid);
        vh.CatPetrol = (CardView) findViewById(R.id.CatPetrol);

        // Set up the search bar
        vh.SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        vh.CatElectric.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenCatList("electric");
            }
        });

        vh.CatHybrid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenCatList("hybrid");
            }
        });

        vh.CatPetrol.setOnClickListener(new View.OnClickListener(){
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