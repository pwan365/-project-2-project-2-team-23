package com.softeng306.p2.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.Model.TagModel;
import com.softeng306.p2.R;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    ArrayList<TagModel> tagModels;
    ArrayList<String> onTags;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        ToggleButton tagTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagBtn);
        }
    }

    public TagAdapter(Context context, ArrayList<TagModel> tagModels){
        this.context = context;
        this.tagModels = tagModels;
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
                onTags.add((String)holder.tagTextView.getText());
            } else {
                holder.tagTextView.setTextColor(Color.BLACK);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagModels.size();
    }

    public ArrayList<String> getOnTags() {
        ArrayList<String> list = new ArrayList<>(onTags);
        onTags.clear();
        return list;
    }


}
