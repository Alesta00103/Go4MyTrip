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

import java.util.List;

public class CostEstimateAdapter extends RecyclerView.Adapter<CostEstimateAdapter.MyViewHolder> {

    Context context;
    List<CostEstimateModel> mData;
    CostEstimateListener costEstimateListener;

    public CostEstimateAdapter(Context context, List<CostEstimateModel> mData, CostEstimateListener costEstimateListener) {
        this.context = context;
        this.mData = mData;
        this.costEstimateListener = costEstimateListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_cost_estimate, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.setCostEstimate(mData.get(position));

        holder.deleteCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                costEstimateListener.onDeleteClicked(mData.get(position),position);
            }
        });
//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                costEstimateListener.onEditClicked(mData.get(position),position);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView cost;
        ImageView deleteCost;
        ConstraintLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            date = itemView.findViewById(R.id.textDateTime);
            cost = itemView.findViewById(R.id.nameItemCost);
            deleteCost = itemView.findViewById(R.id.clearCost);
            layout = itemView.findViewById(R.id.item_cost_layout);
        }

        public void setCostEstimate(CostEstimateModel costEstimate) {
            title.setText(costEstimate.getTitle());
            date.setText(costEstimate.getDate());
            cost.setText(String.valueOf(costEstimate.getAmount()));
        }
    }

}