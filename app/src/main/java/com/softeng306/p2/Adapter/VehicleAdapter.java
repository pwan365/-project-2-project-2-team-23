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
import com.softeng306.p2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    ArrayList<Vehicle> vehicleModels;
    ArrayList<Vehicle> fullList;
    Context context;

    // Initialise object
    public VehicleAdapter(Context context, ArrayList<Vehicle> vehicleModels){
        this.context = context;
        this.vehicleModels = vehicleModels;
        fullList = new ArrayList<>(vehicleModels);
    }

    /**
     * Inner class sets up global variables in a view holder and linked to their id
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        TextView nameTextView, priceTextView;
        ImageView itemImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.vehicleItemTitle);
            priceTextView = itemView.findViewById(R.id.vehicleItemPrice);
            itemImageView = itemView.findViewById(R.id.vehicleItemImage);
        }
    }

    /**
     * Method creates a new view holder and initialises some private fields to be used by RecyclerView.
     * @param parent the ViewGroup which the new View will be added after it is bound to an adapter position.
     * @param viewType type of the view passed in
     * @return a new ViewHolder that holds a view inflated with vehicle_item(s)
     */
    @NonNull
    @Override
    public VehicleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item,parent,false);
        return new ViewHolder(view);
    }

    /**
     * Method displays the data at the specified position
     * @param holder the ViewHolder that is updated to represent the contents of the item at the given position in the data set
     * @param position refers to the index of a specific vehicle in the list of vehicle models (within the adapter's data set)
     */
    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.ViewHolder holder, int position) {

        String fileName = convertNameToFileName(vehicleModels.get(position).getVehicleName())+"_"+1;
        holder.itemImageView.setImageResource(context.getResources().getIdentifier(fileName, "drawable", context.getPackageName()));

        // Sets the vehicle name as the title
        holder.nameTextView.setText(vehicleModels.get(position).getVehicleName());

        // Convert price to display as the conventional format for pricing with commas and 2dp
        String priceStr = String.valueOf(vehicleModels.get(position).getPrice());
        double amount = Double.parseDouble(priceStr);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String strPrice = "$" + formatter.format(amount);

        // Sets the price as text
        holder.priceTextView.setText(strPrice);

        // Click on this item to open the detailActivity, passing it the vehicle name in an intent
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            final CharSequence carTitle = holder.nameTextView.getText();
            intent.putExtra("title",String.valueOf(carTitle));
            context.startActivity(intent);
        });
    }

    /**
     * Method converts a string to replace space and hyphens with underscore symbols
     * @param carTitle a string name
     * @return converted string name
     */
    private String convertNameToFileName(String carTitle){
        return carTitle.toLowerCase(Locale.ROOT).replace(" ","_").replace("-","_");
    }

    /**
     * Returns how many vehicles are in a list of vehicle models
     * @return number of vehicles in the list of vehicle models
     */
    @Override
    public int getItemCount() {
        return vehicleModels.size();
    }

    /**
     * Method filters a vehicle adapter by a Search_Filter
     * @return a filter that constrains data with a filtering pattern
     */
    public Filter getSearchFilter() {
        return searchFilter;
    }

    /**
     * Method creates a filter that constrains data by a vehicle name
     */
    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Initialise final filtered list
            ArrayList<Vehicle> filteredList = new ArrayList<>();
            // Checks search query isn't empty
            if (constraint == null || constraint.length() == 0) {
                // If so add all vehicles from the original list to the final list
                filteredList.addAll(fullList);
            } else {
                // If user had input a query, ensure the string is in a format able to be used to compare
                String filterPattern = constraint.toString().toLowerCase().trim();
                // Loop through the original list of vehicles and add all matching vehicles by
                // name to the final list
                for (Vehicle item : fullList) {
                    if (item.getVehicleName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            // Provides in format to return a filter
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        /**
         * Method invoked in the UI thread to publish the filtering results in the user interface
         * @param constraint the search query constraint used to filter the data
         * @param results the results of the filtering operation
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Updates the new vehicle list resulted from the filter to the vehicle models
            vehicleModels.clear();
            vehicleModels.addAll((ArrayList) results.values);
            // Update the changes in the recyclerview
            notifyDataSetChanged();
        }
    };

}
