package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetailsActivity extends AppCompatActivity {

    static class ViewHolder {
        private TextView titleText;

    }


    String[] imageList;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ViewHolder vh = new ViewHolder();
        vh.titleText = findViewById(R.id.carTitle);
        getData();
        setData(vh);
    }

    private void getData(){
        if(getIntent().hasExtra("title")){
            title = getIntent().getStringExtra("title");
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_LONG).show();
        }

    }

    private void setData(ViewHolder vh){
        vh.titleText.setText(title);
    }
}