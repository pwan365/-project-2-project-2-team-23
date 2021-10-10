package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.denzcoskun.imageslider.constants.ScaleTypes; // important
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.Models.Electric;
import com.softeng306.p2.Models.Hybrid;
import com.softeng306.p2.Models.Petrol;
import com.softeng306.p2.Models.Vehicle;

public class DetailsActivity extends AppCompatActivity implements CoreActivity {

    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }

    class ViewHolder {
        private TextView titleText,descText,priceText;
        private ImageSlider imageSlider;
        private BottomNavigationView bottomNavigationView;
        private ListView detailList;

        public ViewHolder(){
            titleText = findViewById(R.id.carTitle);
            descText = findViewById(R.id.carDescription);
            priceText = findViewById(R.id.carPrice);
            imageSlider = findViewById(R.id.image_slider);
            bottomNavigationView = findViewById(R.id.nav_bar);
            detailList = findViewById(R.id.detailList);
            // Initialise the navigation buttons
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(0);
            menuItem.setChecked(true);
            bottomNavigationView.setOnItemSelectedListener((item) -> {
                switch (item.getItemId()) {
                    case R.id.homeIcon:
                        Intent i1 = new Intent(DetailsActivity.this, MainActivity.class);
                        i1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i1);
                        break;
                    case R.id.searchIcon:
                        Intent searchIntent = new Intent(DetailsActivity.this, SearchActivity.class);
                        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(searchIntent);
                        break;
                    case R.id.favourtiesIcon:
                        Intent favIntent = new Intent(DetailsActivity.this, FavouritesActivity.class);
                        favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(favIntent);
                        break;
                }
                return false;
            });
        }

    }




    String[] imageList;
    String carTitle, carDesc, carPrice;
    List<SlideModel> slideModelList = new ArrayList<>();
    Vehicle vehicle;
    IVehicleDataAccess vda;
    ViewHolder vh;
    List<String> details = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        vh = new ViewHolder();
        VehicleService.getInstance().InjectService(this);
        getData();
    }

    private void getData(){
        if(getIntent().hasExtra("title")){
            carTitle = getIntent().getStringExtra("title");
            vda.getVehicleByName(carTitle, new OnGetVehicleListener() {

                @Override
                public void onCallBack(List<Vehicle> vehicleList) {
                    for (Vehicle v: vehicleList){
                        vehicle = v;
                    }
                    carDesc = vehicle.getDescription();
                    carPrice = "$";
                    carPrice += vehicle.getPrice();
                    getDetails(vehicle);
                    setData();
                }
            });
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_LONG).show();
        }

    }

    private String convertNameToFileName(String carTitle){
        return carTitle.toLowerCase(Locale.ROOT).replace(" ","_");
    }

    private void setData(){
        vh.titleText.setText(carTitle);
        vh.descText.setText(carDesc);
        vh.priceText.setText(carPrice);
        //get file name from car title
        //image slider initialization
        int numImage = 3;
        for(int i = 1; i < numImage+1;i++){
            String fileName = convertNameToFileName(carTitle)+"_"+i;
            int resourceId = getResources().getIdentifier(fileName, "drawable", getPackageName());
            if(resourceId != 0){
                slideModelList.add(new SlideModel(resourceId, ScaleTypes.CENTER_CROP));
            }else{
                slideModelList.add(new SlideModel(R.color.blackGrey, ScaleTypes.CENTER_CROP));
            }
        }
        vh.imageSlider.setImageList(slideModelList,ScaleTypes.CENTER_CROP);
    }

    private void getDetails(Vehicle v){
        details.add("Dimensions" + v.getDimension());
        details.add("Weight: "+ v.getWeight());
        details.add("Manufacture Date: " + v.getManufacturedDate());
        if(v instanceof Electric){
        }else if(v instanceof Petrol){
        }else if(v instanceof Hybrid){
        }
    }

}