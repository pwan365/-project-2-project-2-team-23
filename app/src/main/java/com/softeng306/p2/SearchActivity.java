package com.softeng306.p2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialise the navigation buttons
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.homeIcon:
                    Intent i1 = new Intent(SearchActivity.this, MainActivity.class);
                    i1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i1);
                    break;
                case R.id.searchIcon:
                    break;
                case R.id.favourtiesIcon:
                    Intent i3 = new Intent(SearchActivity.this, FavouritesActivity.class);
                    i3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i3);
                    break;
            }
            return false;
        });
    }
}
