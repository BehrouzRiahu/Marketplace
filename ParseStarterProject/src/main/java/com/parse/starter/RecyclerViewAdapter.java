package com.parse.starter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ClickListener clickListener;
    ArrayList<ItemsModel> itemsArray = null;
    Context context;

    public RecyclerViewAdapter(ArrayList<ItemsModel> itemsArray, Context context){
        this.itemsArray = itemsArray;
        this.context = context;
    }

    public void UpdateData(ArrayList<ItemsModel> itemsArray){
        this.itemsArray = itemsArray;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int i) {

        String[] dataSplitArray = (itemsArray.get(i).getDate()).split(" ");

        if(dataSplitArray[1].endsWith(",")){
            dataSplitArray[1] = dataSplitArray[1].substring(0, dataSplitArray[1].length()-1);
        }

        String date = dataSplitArray[1]+ " " +dataSplitArray[0] + " " + dataSplitArray[2];

        viewHolder.Title.setText(itemsArray.get(i).getTitle());
        viewHolder.Date.setText(date);
        viewHolder.Price.setText(itemsArray.get(i).getPrice());
        Picasso.with(context).load(itemsArray.get(i).getImagefile()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemsArray.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView Title, Price, Date;
        public ImageView imageView;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.view = itemView;
            Date = (TextView) view.findViewById(R.id.adDate);
            Title = (TextView) view.findViewById(R.id.adTitle);
            Price = (TextView) view.findViewById(R.id.adPrice);
            imageView = (ImageView) view.findViewById(R.id.adPic);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null){
                clickListener.OnItemClick(view, getPosition());
            }
        }
    }

    public interface ClickListener{
        public void OnItemClick(View view, int position);
    }
}
