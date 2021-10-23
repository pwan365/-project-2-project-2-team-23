package com.softeng306.p2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.Database.CoreActivity;
import com.softeng306.p2.Database.IVehicleDataAccess;
import com.softeng306.p2.Database.VehicleService;
import com.softeng306.p2.DetailsActivity;
import com.softeng306.p2.DataModel.Vehicle;
import com.softeng306.p2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.ViewHolder> implements CoreActivity {
    ArrayList<Vehicle> vModels;
    Context context;
    IVehicleDataAccess vda;

    public TopAdapter(Context context,ArrayList<Vehicle> topModels){
        this.context = context;
        this.vModels = topModels;
    }


    @NonNull
    @Override
    public TopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAdapter.ViewHolder holder, int position) {
        VehicleService.getInstance();
        VehicleService.InjectService(this);
        String vehicleName = vModels.get(position).getVehicleName();
        vda.getVehicleByName(vehicleName, vehicleList -> {
            for (Vehicle v: vehicleList){
                String fileName = convertNameToFileName(vehicleName)+"_"+1;
                holder.imageView.setImageResource(context.getResources().getIdentifier(fileName, "drawable", context.getPackageName()));
                holder.titleView.setText(vehicleName);

                // Convert price to display as the conventional format for pricing with commas and 2dp
                String priceStr = Float.toString(v.getPrice());
                double amount = Double.parseDouble(priceStr);
                DecimalFormat formatter = new DecimalFormat("#,###.00");
                priceStr = "$"+formatter.format(amount);

                holder.priceView.setText(priceStr);
                holder.topLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    final CharSequence carTitle = holder.titleView.getText();
                    intent.putExtra("title",String.valueOf(carTitle));
                    context.startActivity(intent);
                });
            }

        });

    }

    private String convertNameToFileName(String carTitle){
        return carTitle.toLowerCase(Locale.ROOT).replace(" ","_").replace("-","_");
    }

    @Override
    public int getItemCount() {
        return vModels.size();
    }

    @Override
    public void setDataAccess(IVehicleDataAccess vehicleDataAccess) {
        vda = vehicleDataAccess;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        ImageView imageView;
        TextView titleView,priceView;
        LinearLayout topLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            priceView = itemView.findViewById(R.id.vehicleItemPrice);
            imageView = itemView.findViewById(R.id.vehicleItemImage);
            titleView = itemView.findViewById(R.id.vehicleItemTitle);
            topLayout = itemView.findViewById(R.id.vehicleLayout);
        }
    }


}
