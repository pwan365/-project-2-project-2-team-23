package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CardView CatElectric;
    private CardView CatHybrid;
    private CardView CatPetrol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void OpenCatList(String category) {
        Log.i("MainActivity", "Opening " + category);
    }
}