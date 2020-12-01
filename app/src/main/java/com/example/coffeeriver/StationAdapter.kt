package com.example.test2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeriver.R
import kotlinx.android.synthetic.main.station_item.view.*
import org.w3c.dom.Text

class StationAdapter(
    private val stationList: List<StationItem>,
    private val stationListener: OnStationClickListener
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.station_item,
            parent, false)
        return StationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val currentItem = stationList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView.text = currentItem.title
        holder.imageBtn.setImageResource(currentItem.ImageButton)
    }

    override fun getItemCount() = stationList.size

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val imageView: ImageView = itemView.image_view
        val textView: TextView = itemView.text_view
        val imageBtn: ImageButton = itemView.fav_btn

        init {
            imageView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            //if (position != RecyclerView.NO_POSITION) {
            stationListener.onStationClick(position)
            //}
        }
    }

    interface OnStationClickListener {
        fun onStationClick(position: Int)
    }

}