package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.os.Bundle;

import com.softeng306.p2.Adapter.TopAdapter;
import com.softeng306.p2.Model.TopModel;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    class ViewHolder {
        private CardView CatElectric, CatHybrid, CatPetrol;
        private SearchView SearchBar;
    }

    //Initialize variable
    RecyclerView recyclerView;

    ArrayList<TopModel> topModels;
    TopAdapter topAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign variable
        recyclerView = findViewById(R.id.recycler_view);

        //create integer array
        Integer[] topImg = {R.drawable.hatchback,R.drawable.sedan,R.drawable.pickup_truck,R.drawable.pickup_truck,R.drawable.pickup_truck,R.drawable.pickup_truck};

        //Create string array
        String[] topName = {"Model 3","Model S","Roadster","Model Y","Model X","Cyber Truck"};

        //Initialize arraylist
        topModels =  new ArrayList<>();
        for(int i = 0; i<topImg.length;i++){
            TopModel model = new TopModel(topImg[i],topName[i]);
            topModels.add(model);
        }

        //Design Horizontal Layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Initialize top adapter
        topAdapter = new TopAdapter(MainActivity.this,topModels);
        recyclerView.setAdapter(topAdapter);

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
                SearchEventHandler(searchInput);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchInput) {
                return false;
            }
        });
    }

    // Open search activity with results of the phrase inputted by user
    public void SearchEventHandler(String phrase) {
        Log.i("MainActivity", "Searching for " + phrase);
        /* TO DO */
    }

    // Open list activity based on the category clicked on
    public void CategoryEvenHandler(View v) {
        CardView category = (CardView) v;
        Log.i("MainActivity", "Opening " + category.getContentDescription());
        /* TO DO */
    }
}