package com.aleksandra.go4mytrip;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PackingListAdapter extends RecyclerView.Adapter<PackingListAdapter.MyViewHolder> {
    private Context context;
    private List<PackingModel> mData;
    private static int TYPE_PACK = 1;
    private static int TYPE_SHOP = 2;
    Boolean isPacking;

    public PackingListAdapter(Context context, List<PackingModel> mData, Boolean isPacking) {
        this.context = context;
        this.mData = mData;
        this.isPacking = isPacking;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_PACK) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_packinglist, parent, false);
            return new MyViewHolder(view);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_shopping_list, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPacking) {
            return TYPE_PACK;

        } else {
            return TYPE_SHOP;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(mData.get(position).getName());
        holder.checkBox.setChecked(mData.get(position).getChecked());
        holder.sCheckBox.setChecked(mData.get(position).getCheckedS());
        holder.addToShoppingList.setChecked(mData.get(position).getToBuy());


        holder.addToShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isPressed;
//                Boolean isPressed = mData.get(position).getToBuy();
//                isPressed = !isPressed;

                if (holder.addToShoppingList.isChecked()) {
                    isPressed = true;
                } else {
                    isPressed = false;
                }


                String id = mData.get(position).getItemId();
                Intent intentToBuy = new Intent("toBuy");
                intentToBuy.putExtra("isPressed", isPressed);
                intentToBuy.putExtra("idPckItem", id);
                LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentToBuy);
            }

        });

        holder.deletePackingItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //packingListListener.onClickDelete(mData.get(position), position);
                String id = mData.get(position).getItemId();

                Intent intentPackingItemDelete = new Intent("delete-item");
                intentPackingItemDelete.putExtra("idPackingItemDelete", id);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intentPackingItemDelete);
            }
        });


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isCheck;
                String id = mData.get(position).getItemId();
                String name = mData.get(position).getName();
                String category = mData.get(position).getCategory();
                if (holder.checkBox.isChecked()) {
                    isCheck = true;
                } else {
                    isCheck = false;
                }

                Intent intentCheckBox = new Intent("checkbox");
                intentCheckBox.putExtra("isCheck", isCheck);
                intentCheckBox.putExtra("idPckItem", id);
                intentCheckBox.putExtra("name", name);
                intentCheckBox.putExtra("category", category);
                LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentCheckBox);

            }
        });
        holder.sCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isCheckS;
                String id = mData.get(position).getItemId();

                if (holder.sCheckBox.isChecked()) {
                    isCheckS = true;
                    // holder.name.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    isCheckS = false;
                    // holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }


                Intent intentCheckBoxS = new Intent("checkboxS");
                intentCheckBoxS.putExtra("isCheckS", isCheckS);
                intentCheckBoxS.putExtra("idPckItem", id);
                LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentCheckBoxS);

            }
        });

    }

    //    public String getItem(int position) {
//        return mData.get(position).getItemId();
//    }
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox sCheckBox;
        CheckBox checkBox;
        ImageView deletePackingItem;
        CheckBox addToShoppingList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_item);
            checkBox = itemView.findViewById(R.id.checkBox_item);
            sCheckBox = itemView.findViewById(R.id.sCheckBox_item);
            deletePackingItem = itemView.findViewById(R.id.deletePackingItem);
            addToShoppingList = itemView.findViewById(R.id.addToShoppingList);

        }
    }
}

