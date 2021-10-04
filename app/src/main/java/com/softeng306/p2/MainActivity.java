package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.softeng306.p2.Adapter.TopAdapter;
import com.softeng306.p2.Model.TopModel;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
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
    }
}