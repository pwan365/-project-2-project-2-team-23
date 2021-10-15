package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.denzcoskun.imageslider.constants.ScaleTypes; // important
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.softeng306.p2.Adapter.DetailAdapter;
import com.softeng306.p2.Adapter.TopAdapter;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.Listeners.OnGetUserListener;
import com.softeng306.p2.Listeners.OnGetVehicleListener;
import com.softeng306.p2.ViewModel.DetailModel;
import com.softeng306.p2.ViewModel.TopModel;
import com.softeng306.p2.DataModel.Electric;
import com.softeng306.p2.DataModel.Hybrid;
import com.softeng306.p2.DataModel.Petrol;
import com.softeng306.p2.DataModel.Vehicle;

public class DetailsActivity extends AppCompatActivity implements CoreActivity {
    /**
     * initialize the database object
     * @param vehicleDataAccess
     */
    @Override
    public void SetDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }

    class ViewHolder {
        private TextView titleText,descText,priceText;
        private ImageSlider imageSlider;
        private BottomNavigationView bottomNavigationView;
        private ListView detailList;
        private RecyclerView recyclerView;
        private ImageButton listBackButton;
        private LikeButton likeButton;

        public ViewHolder(){
            titleText = findViewById(R.id.carTitle);
            descText = findViewById(R.id.carDescription);
            priceText = findViewById(R.id.carPrice);
            imageSlider = findViewById(R.id.image_slider);
            bottomNavigationView = findViewById(R.id.nav_bar);
            detailList = findViewById(R.id.detailList);
            recyclerView = findViewById(R.id.related_view);
            listBackButton = findViewById(R.id.listBackButton);
            likeButton = findViewById(R.id.heart_button);

            //Initialise the back button
            listBackButton.setOnClickListener(view -> GoBack());

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

    //Initialize fields
    String carTitle, carDesc, carPrice;
    List<SlideModel> slideModelList = new ArrayList<>();
    Vehicle vehicle;
    IVehicleDataAccess vda;
    ViewHolder vh;
    List<DetailModel> details = new ArrayList();
    ArrayList<TopModel> topModels = new ArrayList<>();
    String relatedVehicleType = "Electric";
    User userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        vh = new ViewHolder();
        VehicleService.getInstance().InjectService(this);
        getData();
    }

    /**
     * retrieve data from database
     */
    private void getData(){
        //get vehicle object from database depend on the name of the vehicle
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
                    carPrice += (int)vehicle.getPrice();
                    //set up data once the vehicle is retrieved
                    getDetails(vehicle);
                    setDetails();
                    setData();
                    addRelatedView();
                    vda.getFavourites(new OnGetUserListener() {
                        @Override
                        public void onCallBack(User user) {
                            userDetail = user;
                            checkFavourite(userDetail);
                        }
                    });
                }
            });
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_LONG).show();
        }



    }

    /**
     * A helper method that convert name of car to image name
     * @param carTitle
     * @return string in the format of image file name
     */
    private String convertNameToFileName(String carTitle){
        return carTitle.toLowerCase(Locale.ROOT).replace(" ","_").replace("-","_");
    }

    /**
     * method for the back button
     */
    public void GoBack() {
        finish();
    }

    /**
     * On activity initialization, check if the vehicle is in the list of favourite
     * vehicle for the user. Initialize the like button based on the information.
     * @param user
     */
    private void checkFavourite(User user){
        List<Integer> favouriteList = user.getFavourites();
        int vehicleId = vehicle.getId();
        //if vehicle is favourite, like button is liked
        for(int i:favouriteList){
            if(i==vehicleId){
                vh.likeButton.setLiked(true);
            }
        }

        //Initialize the like button for adding and removing vehicle from favourite list
        vh.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                vda.addToFavourites(vehicle.getId());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                vda.removeFromFavourites(vehicle.getId());

            }
        });
    }

    private void setData(){
        vh.titleText.setText(carTitle);
        vh.descText.setText(carDesc);
        vh.priceText.setText(carPrice);

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
        details.add(new DetailModel("Dimension",v.getDimension()));
        details.add(new DetailModel("Weight",v.getWeight()+"kg"));
        details.add(new DetailModel("Manufacture Date",v.getManufacturedDate()+""));

        //Dependency injection to the vehicle object to display different info based on different object
        if(v instanceof Electric){
            details.add(new DetailModel("Battery Capacity",((Electric) v).getBatteryCapacity()));
            details.add(new DetailModel("Charging Time",((Electric) v).getChargingTime()));
            details.add(new DetailModel("Travel Distance",((Electric) v).getTravelDistance()));
        }else if(v instanceof Petrol){
            details.add(new DetailModel("Tank Capacity",((Petrol) v).getTankCapacity()+"L"));
        }else if(v instanceof Hybrid){
            details.add(new DetailModel("PHEV",((Hybrid) v).getIsPHEV()+""));
            details.add(new DetailModel("Charging Time",((Hybrid) v).getChargingTime()+"Mins"));
        }
    }

    /**
     * set up the details view with adapter
     */
    private void setDetails(){
        DetailAdapter detailAdapter = new DetailAdapter(vh.detailList.getContext(), details);
        vh.detailList.setAdapter(detailAdapter);
    }

    /**
     * created the suggestion view based on the type of the vehicle
     */
    private void addRelatedView(){
        // Design Horizontal Layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL,false);
        vh.recyclerView.setLayoutManager(layoutManager);
        vh.recyclerView.setItemAnimator(new DefaultItemAnimator());


        if(vehicle instanceof Electric){
            relatedVehicleType = "Electric";
            vda.getCategoryVehicles(relatedVehicleType, new OnGetVehicleListener(){

                @Override
                public void onCallBack(List<Vehicle> vehicleList) {
                    for(Vehicle v : vehicleList){
                        if(!v.getVehicleName().equals(vehicle.getVehicleName())){
                            topModels.add(new TopModel(v.getVehicleName()));
                        }
                    }
                    TopAdapter topAdapter = new TopAdapter(DetailsActivity.this,topModels);
                    vh.recyclerView.setAdapter(topAdapter);
                }
            });
        }else if(vehicle instanceof Petrol){
            relatedVehicleType = "Petrol";
            vda.getCategoryVehicles(relatedVehicleType, new OnGetVehicleListener(){

                @Override
                public void onCallBack(List<Vehicle> vehicleList) {
                    for(Vehicle v : vehicleList){
                        if(!v.getVehicleName().equals(vehicle.getVehicleName())){
                            topModels.add(new TopModel(v.getVehicleName()));
                        }
                    }
                    TopAdapter topAdapter = new TopAdapter(DetailsActivity.this,topModels);
                    vh.recyclerView.setAdapter(topAdapter);
                }
            });
        }else if(vehicle instanceof Hybrid){
            relatedVehicleType = "Hybrid";
            vda.getCategoryVehicles(relatedVehicleType, new OnGetVehicleListener(){

                @Override
                public void onCallBack(List<Vehicle> vehicleList) {
                    for(Vehicle v : vehicleList){
                        if(!v.getVehicleName().equals(vehicle.getVehicleName())){
                            topModels.add(new TopModel(v.getVehicleName()));
                        }
                    }
                    TopAdapter topAdapter = new TopAdapter(DetailsActivity.this,topModels);
                    vh.recyclerView.setAdapter(topAdapter);
                }
            });
        }
    }
}