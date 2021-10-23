package com.softeng306.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.denzcoskun.imageslider.constants.ScaleTypes; // important
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.softeng306.p2.Adapter.DetailAdapter;
import com.softeng306.p2.Adapter.TagAdapter;
import com.softeng306.p2.Adapter.TopAdapter;
import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.DataModel.User;
import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.ViewModel.DetailModel;
import com.softeng306.p2.DataModel.Electric;
import com.softeng306.p2.DataModel.Hybrid;
import com.softeng306.p2.DataModel.Petrol;
import com.softeng306.p2.DataModel.Vehicle;

public class DetailsActivity extends AppCompatActivity implements CoreActivity {
    /**
     * initialize the database object
     * @param vehicleDataAccess Interface that provides access to the database
     */
    @Override
    public void setDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }

    class ViewHolder {
        private final TextView titleText,descText,priceText;
        private final ImageSlider imageSlider;
        private final ListView detailList;
        private final RecyclerView recyclerView;
        private final LikeButton likeButton;

        public ViewHolder(){
            titleText = findViewById(R.id.carTitle);
            descText = findViewById(R.id.carDescription);
            priceText = findViewById(R.id.carPrice);
            imageSlider = findViewById(R.id.image_slider);
            BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
            detailList = findViewById(R.id.detailList);
            recyclerView = findViewById(R.id.relatedView);
            ImageButton detailBackButton = findViewById(R.id.detailBackButton);
            likeButton = findViewById(R.id.heartBtn);

            //Initialise the back button
            detailBackButton.setOnClickListener(view -> goBack());

            // Initialise the navigation buttons
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(0);
            menuItem.setChecked(true);
            bottomNavigationView.setOnItemSelectedListener((item) -> {
                int id = item.getItemId();
                if ( id == R.id.homeIcon ) {
                    Intent i1 = new Intent(DetailsActivity.this, MainActivity.class);
                    i1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i1);
                    overridePendingTransition(0, R.anim.slide_to_right);
                }else if ( id == R.id.searchIcon) {
                    Intent searchIntent = new Intent(DetailsActivity.this, SearchActivity.class);
                    searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(searchIntent);
                    overridePendingTransition(0, R.anim.slide_to_right);
                } else if ( id == R.id.favourtiesIcon ) {
                    Intent favIntent = new Intent(DetailsActivity.this, FavouritesActivity.class);
                    favIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(favIntent);
                    overridePendingTransition(0, R.anim.slide_to_right);
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
    List<DetailModel> details = new ArrayList<>();
    ArrayList<Vehicle> vModels = new ArrayList<>();
    String relatedVehicleType = "Electric";
    User userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initLoading();
        vh = new ViewHolder();
        VehicleService.getInstance();
        VehicleService.InjectService(this);
        getData();
        overridePendingTransition(R.anim.slide_from_right, R.anim.no_movement);
    }

    private void initLoading() {
        CardView cardView = findViewById(R.id.progressCard);
        cardView.postDelayed(new Runnable() {
            public void run() {
                cardView.animate()
                        .translationY(cardView.getHeight())
                        .alpha(0.0f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardView.setVisibility(View.GONE);
                            }
                        });
            }
        }, 1200);
    }

    /**
     * retrieve data from database and initialize view at call back
     */
    private void getData(){
        //get vehicle object from database depend on the name of the vehicle
        if(getIntent().hasExtra("title")){
            carTitle = getIntent().getStringExtra("title");
            vda.getVehicleByName(carTitle, vehicleList -> {
                for (Vehicle v: vehicleList){
                    vehicle = v;
                }
                carDesc = vehicle.getDescription();

                // Convert price to display as the conventional format for pricing with commas and 2dp
                String priceStr = Float.toString(vehicle.getPrice());
                double amount = Double.parseDouble(priceStr);
                DecimalFormat formatter = new DecimalFormat("#,###.00");
                carPrice = "$"+formatter.format(amount);
                //set up data once the vehicle is retrieved
                getDetails(vehicle);
                setDetails();
                setData();
                setTags();
                addRelatedView();
                vda.getFavourites(user -> {
                    userDetail = user;
                    checkFavourite(userDetail);
                });
            });
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * A helper method that convert name of car to image name
     * @param carTitle string name of the vehicle
     * @return string in the format of image file name
     */
    private String convertNameToFileName(String carTitle){
        return carTitle.toLowerCase(Locale.ROOT).replace(" ","_").replace("-","_");
    }

    /**
     * Method for the back button to return to previous activity
     */
    public void goBack() {
        finish();
        overridePendingTransition(R.anim.no_movement, R.anim.slide_to_right);
    }

    /**
     * On activity initialization, check if the vehicle is in the list of favourite
     * vehicle for the user. Initialize the like button based on the information.
     * @param user an object of the User model
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

    /**
     * set text view and image slider with corresponding vehicle information
     */
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

    /**
     * display content of vehicle in a list view based on the vehicle type retrieved
     * @param v an object of Vehicle model
     */
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

        //show child vehicle object information
        if(vehicle instanceof Electric){
            relatedVehicleType = "Electric";
            vda.getCategoryVehicles(relatedVehicleType, vehicleList -> {
                for(Vehicle v : vehicleList){
                    if(!v.getVehicleName().equals(vehicle.getVehicleName())){
                        vModels.add(new Vehicle(v.getVehicleName(),v.getPrice()));
                    }
                }
                TopAdapter topAdapter = new TopAdapter(DetailsActivity.this,vModels);
                vh.recyclerView.setAdapter(topAdapter);
            });
        }else if(vehicle instanceof Petrol){
            relatedVehicleType = "Petrol";
            vda.getCategoryVehicles(relatedVehicleType, vehicleList -> {
                for(Vehicle v : vehicleList){
                    if(!v.getVehicleName().equals(vehicle.getVehicleName())){
                        vModels.add(new Vehicle(v.getVehicleName(),v.getPrice()));
                    }
                }
                TopAdapter topAdapter = new TopAdapter(DetailsActivity.this,vModels);
                vh.recyclerView.setAdapter(topAdapter);
            });
        }else if(vehicle instanceof Hybrid){
            relatedVehicleType = "Hybrid";
            vda.getCategoryVehicles(relatedVehicleType, vehicleList -> {
                for(Vehicle v : vehicleList){
                    if(!v.getVehicleName().equals(vehicle.getVehicleName())){
                        vModels.add(new Vehicle(v.getVehicleName(),v.getPrice()));
                    }
                }
                TopAdapter topAdapter = new TopAdapter(DetailsActivity.this,vModels);
                vh.recyclerView.setAdapter(topAdapter);
            });
        }
    }

    /**
     * Display the set of tags associated to the vehicle in a recyclerview
     */
    private void setTags(){
        LinearLayout tagsContainer = findViewById(R.id.detailTagContainer);
        tagsContainer.setVisibility(View.VISIBLE);

        Map<String, String> tags = vehicle.getTags();
        List<String> tagNames = new ArrayList<>(tags.keySet());

        if(tagNames.isEmpty()) {
            TextView title = findViewById(R.id.featuresTitle);
            View divider = findViewById(R.id.divider3);
            title.setVisibility(View.GONE);
            tagsContainer.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            // Initialize arraylist
            ArrayList<Tag> tagModels = new ArrayList<>();
            for (String tag : tagNames) {
                Tag model = new Tag(tag);
                tagModels.add(model);
            }

            RecyclerView tagRecyclerView = new RecyclerView(DetailsActivity.this);
            int id = View.generateViewId();
            tagRecyclerView.setId(id);

            // Design horizontal layout
            tagRecyclerView.setLayoutManager(new LinearLayoutManager(
                    this, GridLayoutManager.HORIZONTAL, false));
            tagRecyclerView.setItemAnimator(new DefaultItemAnimator());

            // Initialize adapter
            ColorStateList colour = ColorStateList.valueOf(getResources().getColor(R.color.yellow));
            TagAdapter tagAdapter = new TagAdapter(
                    DetailsActivity.this, tagModels, colour, true);
            tagRecyclerView.setAdapter(tagAdapter);

            tagsContainer.addView(tagRecyclerView);
        }
    }
}