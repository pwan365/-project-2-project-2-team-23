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

import com.softeng306.p2.ViewModel.TagModel;
import com.softeng306.p2.R;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    ArrayList<TagModel> tagModels;
    ArrayList<String> onTags;
    Context context;
    ColorStateList CatColour;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        ToggleButton tagTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagBtn);
        }
    }

    public TagAdapter(Context context, ArrayList<TagModel> tagModels, ColorStateList CatColour){
        this.context = context;
        this.tagModels = tagModels;
        this.CatColour = CatColour;
    }

    @NonNull
    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        onTags = new ArrayList<>();
        //create view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {
        holder.tagTextView.setText(tagModels.get(position).getTName());
        holder.tagTextView.setTextOff(tagModels.get(position).getTName());
        holder.tagTextView.setTextOn(tagModels.get(position).getTName());

        holder.tagTextView.setOnClickListener(view -> {
            if (holder.tagTextView.isChecked()) {
                holder.tagTextView.setTextColor(Color.WHITE);
                holder.tagTextView.setBackgroundTintList(CatColour);

                onTags.add((String)holder.tagTextView.getText());
            } else {
                holder.tagTextView.setTextColor(Color.BLACK);
                holder.tagTextView.setBackgroundTintList(ColorStateList.valueOf(
                        view.getResources().getColor(R.color.lightGrey)));

                onTags.remove(holder.tagTextView.getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagModels.size();
    }

    public ArrayList<String> getOnTags() {
        return onTags;
    }


}
