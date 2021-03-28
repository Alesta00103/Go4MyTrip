package com.aleksandra.go4mytrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {
    List<PlaceModel> mData;
    Context context;
    PlaceListener placeListener;

    public PlaceAdapter(Context context, List<PlaceModel> mData, PlaceListener placeListener) {

        this.context = context;
        this.mData = mData;
        this.placeListener = placeListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_place, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.placeName.setText(mData.get(position).getName());
        holder.address.setText(mData.get(position).getAddress());
        holder.setTextTimeAndDate(mData.get(position));

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeListener.onClickDelete(mData.get(position), position);
            }
        });

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                placeListener.onLongClicked(mData.get(position), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView placeName;
        TextView address;
        ConstraintLayout layout;
        TextView date;
        TextView time;
        ImageView clear;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            placeName = itemView.findViewById(R.id.placeName);
            address = itemView.findViewById(R.id.addressName);
            layout = itemView.findViewById(R.id.item_note_layout);
            date = itemView.findViewById(R.id.textDate);
            time = itemView.findViewById(R.id.textTime);
            clear = itemView.findViewById(R.id.clearPlace);
        }

        void setTextTimeAndDate(PlaceModel placeModel) {
            if (placeModel.getDate() != null) {
                date.setText(placeModel.getDate());
            } else {
                date.setText("Date");
            }
            if (placeModel.getTime() != null) {
                time.setText(placeModel.getTime());
            } else {
                time.setText("Time");
            }

        }
    }


}
