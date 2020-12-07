package com.example.coffeeriver


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.rotationMatrix
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.station_item.*
import kotlinx.android.synthetic.main.station_item.view.*

class MainActivity : AppCompatActivity(), StationAdapter.OnStationClickListener {
    private var stationList = mutableListOf<StationItem>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)
        initData()

        recycler_view.adapter = StationAdapter(stationList, this)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

    }

    override fun onStationClick(position: Int) {
        Toast.makeText(this, "${stationList[position].title} was selected", Toast.LENGTH_SHORT).show()
        val intent = ChannelActivity.newIntent(this@MainActivity, position)
        startActivity(intent)
    }

    override fun onFavBtnClick(position: Int) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("MODIFIED", true).apply()
        if(sharedPreferences.getBoolean("STATION_ID_$position", false))
        {
            editor.putBoolean("STATION_ID_$position", false)
            stationList[position].ImageButton = R.drawable.ic_favorite_shadow_45
            Toast.makeText(this, "${stationList[position].title} removed from favorite", Toast.LENGTH_SHORT).show()
        }
        else {
            editor.putBoolean("STATION_ID_$position", true)
            stationList[position].ImageButton = R.drawable.ic_favorite_red_45
            Toast.makeText(this, "${stationList[position].title} added as favorite", Toast.LENGTH_SHORT).show()
        }
        editor.commit()
        val saveState = recycler_view.layoutManager?.onSaveInstanceState()
        recycler_view.adapter = StationAdapter(stationList, this)
        recycler_view.layoutManager?.onRestoreInstanceState(saveState)
    }


    private fun initData() : List<StationItem> {
        sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean("MODIFIED", false)) {
            stationList.add(StationItem(R.drawable.sr_icon_1, "Sveriges Radio", isFavorite("STATION_ID_0")))
            stationList.add(StationItem(R.drawable.placeholder_image, "Exempel 1", isFavorite("STATION_ID_1")))
            stationList.add(StationItem(R.drawable.placeholder_image, "Exempel 2", isFavorite("STATION_ID_2")))
            stationList.add(StationItem(R.drawable.placeholder_image, "Exempel 3", isFavorite("STATION_ID_3")))
        }
        else {
            stationList.add(StationItem(R.drawable.sr_icon_1, "Sveriges Radio", R.drawable.ic_favorite_red_45))
            stationList.add(StationItem(R.drawable.placeholder_image, "Exempel 1", R.drawable.ic_favorite_shadow_45))
            stationList.add(StationItem(R.drawable.placeholder_image, "Exempel 2", R.drawable.ic_favorite_red_45))
            stationList.add(StationItem(R.drawable.placeholder_image, "Exempel 3", R.drawable.ic_favorite_shadow_45))
        }
        return stationList
    }


    private fun isFavorite(key: String): Int {
        val favorite = sharedPreferences.getBoolean(key, false)
        if(favorite) {
            return R.drawable.ic_favorite_red_45
        }
        return R.drawable.ic_favorite_shadow_45
    }

}