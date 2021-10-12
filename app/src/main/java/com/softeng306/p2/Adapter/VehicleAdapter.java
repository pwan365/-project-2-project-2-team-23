package com.softeng306.p2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.DetailsActivity;
import com.softeng306.p2.Model.VehicleModel;
import com.softeng306.p2.R;

import java.util.ArrayList;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    ArrayList<VehicleModel> vehicleModels;
    Context context;

    public VehicleAdapter(Context context, ArrayList<VehicleModel> vehicleModels){
        this.context = context;
        this.vehicleModels = vehicleModels;
    }


    @NonNull
    @Override
    public VehicleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.ViewHolder holder, int position) {
        //set Logo to ImageView
        holder.nameTextView.setText(vehicleModels.get(position).getVName());
        String price = "$" + vehicleModels.get(position).getVPrice().toString();
        holder.priceTextView.setText(price);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            final CharSequence carTitle = holder.nameTextView.getText();
            intent.putExtra("title",String.valueOf(carTitle));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicleModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        TextView nameTextView, priceTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.listItemTitle);
            priceTextView = itemView.findViewById(R.id.listItemPrice);
        }
    }
}
