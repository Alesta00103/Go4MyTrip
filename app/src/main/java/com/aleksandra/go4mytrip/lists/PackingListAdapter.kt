package com.aleksandra.go4mytrip.lists

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.aleksandra.go4mytrip.R

class PackingListAdapter(private val context: Context, private val mData: MutableList<PackingModel>, private var isPacking: Boolean) : RecyclerView.Adapter<PackingListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        return if (viewType == TYPE_PACK) {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.item_packinglist, parent, false)
            MyViewHolder(view)
        } else {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.item_shopping_list, parent, false)
            MyViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPacking) {
            TYPE_PACK
        } else {
            TYPE_SHOP
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = mData[position].name
        holder.checkBox.isChecked = mData[position].checked!!
        holder.sCheckBox.isChecked = mData[position].checkedS!!
        holder.addToShoppingList.isChecked = mData[position].toBuy!!
        holder.addToShoppingList.setOnClickListener {
            val isPressed: Boolean = holder.addToShoppingList.isChecked
            val id = mData[position].itemId
            val intentToBuy = Intent("toBuy")
            intentToBuy.putExtra("isPressed", isPressed)
            intentToBuy.putExtra("idPckItem", id)
            LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentToBuy)
        }
        holder.deletePackingItem.setOnClickListener {
            val id = mData[position].itemId
            val intentPackingItemDelete = Intent("delete-item")
            intentPackingItemDelete.putExtra("idPackingItemDelete", id)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentPackingItemDelete)
        }
        holder.checkBox.setOnClickListener {
            val id = mData[position].itemId
            val name = mData[position].name
            val category = mData[position].category
            val isCheck: Boolean = holder.checkBox.isChecked
            val intentCheckBox = Intent("checkbox")
            intentCheckBox.putExtra("isCheck", isCheck)
            intentCheckBox.putExtra("idPckItem", id)
            intentCheckBox.putExtra("name", name)
            intentCheckBox.putExtra("category", category)
            LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentCheckBox)
        }
        holder.sCheckBox.setOnClickListener {
            val id = mData[position].itemId
            val isCheckS: Boolean = holder.sCheckBox.isChecked
            val intentCheckBoxS = Intent("checkboxS")
            intentCheckBoxS.putExtra("isCheckS", isCheckS)
            intentCheckBoxS.putExtra("idPckItem", id)
            LocalBroadcastManager.getInstance(context).sendBroadcastSync(intentCheckBoxS)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var sCheckBox: CheckBox
        var checkBox: CheckBox
        var deletePackingItem: ImageView
        var addToShoppingList: CheckBox

        init {
            name = itemView.findViewById(R.id.text_item)
            checkBox = itemView.findViewById(R.id.checkBox_item)
            sCheckBox = itemView.findViewById(R.id.sCheckBox_item)
            deletePackingItem = itemView.findViewById(R.id.deletePackingItem)
            addToShoppingList = itemView.findViewById(R.id.addToShoppingList)
        }
    }

    companion object {
        private const val TYPE_PACK = 1
        private const val TYPE_SHOP = 2
    }
}