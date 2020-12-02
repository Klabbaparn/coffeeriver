package com.example.coffeeriver

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import me.relex.circleindicator.CircleIndicator3
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.StationAdapter
import com.example.test2.StationItem

class MainActivity : AppCompatActivity(), StationAdapter.OnStationClickListener {
    private val stationList = generateDummyList(20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)

        recycler_view.adapter = StationAdapter(stationList, this)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
    }

    override fun onStationClick(position: Int) {
        Toast.makeText(this, "Station $position clicked", Toast.LENGTH_SHORT).show()
        val clickedStation = stationList[position]
        //clickedStation.title = "Clicked"
    }

    private fun generateDummyList(size: Int) : List<StationItem> {
        val list = ArrayList<StationItem>()

        for (i in 0 until size) {
            val drawable = when ( i % 3) {
                0 -> R.drawable.sr_icon_1
                1 -> R.drawable.bandit_rock_icon
                2 -> R.drawable.finska_icon
                else -> R.drawable.rix_fm_icon
            }

            val item = StationItem(drawable, "Station $i", R.drawable.ic_favorite_red_24)
            list += item
        }
        return list
    }
}