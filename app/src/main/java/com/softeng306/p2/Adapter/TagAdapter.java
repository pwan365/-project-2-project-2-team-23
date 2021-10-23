package com.softeng306.p2.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.DataModel.Tag;
import com.softeng306.p2.R;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    ArrayList<Tag> tagModels;
    ArrayList<String> onTags;
    Context context;
    ColorStateList catColour;
    Boolean isOn;

    /**
     * Initialise object
     * @param context provides information of the current state of our application
     * @param tagModels an array list of tag models
     * @param catColour colour index number belonging to the category
     */
    public TagAdapter(Context context, ArrayList<Tag> tagModels, ColorStateList catColour, Boolean isOn) {
        this.context = context;
        this.tagModels = tagModels;
        this.catColour = catColour;
        this.isOn = isOn;
    }

    /**
     * Inner class sets up global variables in a view holder and linked to their id
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        ToggleButton tagTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagBtn);
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
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // initialise arrays
        onTags = new ArrayList<>();
        // create view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item,parent,false);
        return new ViewHolder(view);
    }

    /**
     * Method displays the data at the specified position
     * @param holder the ViewHolder that is updated to represent the contents of the item at the given position in the data set
     * @param position refers to the index of a specific tags in the list of tag models (within the adapter's data set)
     */
    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {
        holder.tagTextView.setText(tagModels.get(position).getTagName());
        holder.tagTextView.setTextOff(tagModels.get(position).getTagName());
        holder.tagTextView.setTextOn(tagModels.get(position).getTagName());

        if (isOn) {
            holder.tagTextView.setTextColor(Color.WHITE);
            holder.tagTextView.setBackgroundTintList(catColour);
            holder.tagTextView.setEnabled(false);
        } else {
            holder.tagTextView.setOnClickListener(view -> {
                if (holder.tagTextView.isChecked()) {
                    holder.tagTextView.setTextColor(Color.WHITE);
                    holder.tagTextView.setBackgroundTintList(catColour);

                    onTags.add((String) holder.tagTextView.getText());
                } else {
                    holder.tagTextView.setTextColor(Color.BLACK);
                    holder.tagTextView.setBackgroundTintList(ColorStateList.valueOf(
                            view.getResources().getColor(R.color.lightGrey)));

                    onTags.remove((String) holder.tagTextView.getText());
                }
            });
        }
    }

    /**
     * Returns how many tags are in a list of tag models
     * @return number of tags in the list of tag models
     */
    @Override
    public int getItemCount() {
        return tagModels.size();
    }

    /**
     * Getter method for the tags that have been selected by user
     * @return Array list of tags names that are toggled on
     */
    public ArrayList<String> getOnTags() {
        return onTags;
    }


}
