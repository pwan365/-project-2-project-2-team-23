package com.softeng306.p2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softeng306.p2.DetailsActivity;
import com.softeng306.p2.Model.TopModel;
import com.softeng306.p2.R;

import java.util.ArrayList;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.ViewHolder> {
    ArrayList<TopModel> topModels;
    Context context;

    public TopAdapter(Context context,ArrayList<TopModel> topModels){
        this.context = context;
        this.topModels = topModels;
    }


    @NonNull
    @Override
    public TopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAdapter.ViewHolder holder, int position) {
        //set Logo to ImageView
        holder.imageView.setImageResource(topModels.get(position).getTpImg());
        holder.titleView.setText(topModels.get(position).getTpName());
        holder.topLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                final CharSequence carTitle = holder.titleView.getText();
                intent.putExtra("title",String.valueOf(carTitle));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Initialize variable
        ImageView imageView;
        TextView titleView,priceView;
        LinearLayout topLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            priceView = itemView.findViewById(R.id.top_price_view);
            imageView = itemView.findViewById(R.id.top_image_view);
            titleView = itemView.findViewById(R.id.top_name_view);
            topLayout = itemView.findViewById(R.id.topLayout);
        }
    }
}
