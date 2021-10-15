package com.softeng306.p2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.DataModel.Vehicle;
import com.softeng306.p2.DetailsActivity;
import com.softeng306.p2.ViewModel.VehicleModel;
import com.softeng306.p2.R;

import java.util.ArrayList;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    ArrayList<VehicleModel> vehicleModels;
    ArrayList<VehicleModel> FullList;
    Context context;

    public VehicleAdapter(Context context, ArrayList<VehicleModel> vehicleModels){
        this.context = context;
        this.vehicleModels = vehicleModels;
        FullList = new ArrayList<>(vehicleModels);
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

        String fileName = convertNameToFileName(vehicleModels.get(position).getVName())+"_"+1;
        holder.itemImageView.setImageResource(context.getResources().getIdentifier(fileName, "drawable", context.getPackageName()));

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


    private String convertNameToFileName(String carTitle){
        return carTitle.toLowerCase(Locale.ROOT).replace(" ","_").replace("-","_");
    }

    @Override
    public int getItemCount() {
        return vehicleModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        TextView nameTextView, priceTextView;
        ImageView itemImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.listItemTitle);
            priceTextView = itemView.findViewById(R.id.listItemPrice);
            itemImageView = itemView.findViewById(R.id.listItemImage);
        }
    }

    public Filter getSearchFilter() {
        return Searched_Filter;
    }

    private Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<VehicleModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(FullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (VehicleModel item : FullList) {
                    if (item.getVName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            vehicleModels.clear();
            vehicleModels.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

}
