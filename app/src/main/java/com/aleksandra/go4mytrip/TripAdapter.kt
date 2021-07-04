package com.aleksandra.go4mytrip

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TripAdapter(private val context: Context, private val mData: MutableList<TripModel>?, private val tripsListener: TripsListener) : RecyclerView.Adapter<TripAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        val mInflater = LayoutInflater.from(context)
        view = mInflater.inflate(R.layout.item_trip, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mData?.let{
            holder.textViewTitle.text = mData[position].title
            holder.imgTrip.setImageResource(mData[position].imageTrip)
            holder.dateStart.text = mData[position].tripDate
            holder.dateEnd.text = mData[position].tripDateEnd
            holder.imgTrip.setOnClickListener {
                val id = mData[position].tripId
                val title = mData[position].title
                val image = mData[position].imageTrip
                val dateS = mData[position].tripDate
                val dateE = mData[position].tripDateEnd
                val timeStart = mData[position].timeStart
                val timeEnd = mData[position].timeEnd
                val intent = Intent(context, DetailTrip::class.java)
                intent.putExtra("idTripToDetail", id)
                intent.putExtra("title", title)
                intent.putExtra("image", image)
                intent.putExtra("dateS", dateS)
                intent.putExtra("dateE", dateE)
                intent.putExtra("timeStart", timeStart)
                intent.putExtra("timeEnd", timeEnd)
                context.startActivity(intent)
            }
            holder.imgTrip.setOnLongClickListener {
                tripsListener.onLongClicked(mData[position], position)
                true
            }
        }

    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.nameTrip)
        var imgTrip: ImageButton = itemView.findViewById(R.id.imagexTrip)
        var dateStart: TextView = itemView.findViewById(R.id.startDate)
        var dateEnd: TextView = itemView.findViewById(R.id.endDate)

    }
}