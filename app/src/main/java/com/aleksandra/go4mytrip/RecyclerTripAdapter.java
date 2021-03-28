package com.aleksandra.go4mytrip;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerTripAdapter extends RecyclerView.Adapter<RecyclerTripAdapter.MyViewHolder> {
    private Context context;
    private List<TripModel> mData;
    private TripsListener tripsListener;


    public RecyclerTripAdapter(Context context, List<TripModel> mData, TripsListener tripsListener) {
        this.context = context;
        this.mData = mData;
        this.tripsListener = tripsListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.item_trip, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView_title.setText(mData.get(position).getTitle());
        holder.img_trip.setImageResource(mData.get(position).getImageTrip());
        holder.dateStart.setText(mData.get(position).getTripDate());
        holder.dateEnd.setText(mData.get(position).getTripDateEnd());
        holder.img_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = mData.get(position).getTripId();
                String title = mData.get(position).getTitle();
                int image = mData.get(position).getImageTrip();
                String dateS = mData.get(position).getTripDate();
                String dateE = mData.get(position).getTripDateEnd();
                String timeStart = mData.get(position).getTimeStart();
                String timeEnd = mData.get(position).getTimeEnd();

                Intent intent = new Intent(context, DetailTrip.class);
                intent.putExtra("idTripToDetail", id);
                intent.putExtra("title", title);
                intent.putExtra("image", image);
                intent.putExtra("dateS", dateS);
                intent.putExtra("dateE", dateE);
                intent.putExtra("timeStart", timeStart);
                intent.putExtra("timeEnd", timeEnd);
                context.startActivity(intent);
            }
        });
        holder.img_trip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tripsListener.onLongClicked(mData.get(position), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView_title;
        ImageButton img_trip;
        TextView dateStart;
        TextView dateEnd;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_title = itemView.findViewById(R.id.nameTrip);
            img_trip = itemView.findViewById(R.id.imagexTrip);
            dateStart = itemView.findViewById(R.id.startDate);
            dateEnd = itemView.findViewById(R.id.endDate);

        }

    }

}
