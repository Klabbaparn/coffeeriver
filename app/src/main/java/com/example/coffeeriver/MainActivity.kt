package com.example.coffeeriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import me.relex.circleindicator.CircleIndicator3

class MainActivity : AppCompatActivity() {


    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager2.adapter = ViewPagerAdapter(titlesList,descList,imagesList)
        view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        indicator.setViewPager(view_pager2)

        appendToList("Sveriges Radio", "Local and countrywide broadcast", R.drawable.sr_icon)
        appendToList("Finnish Radio", "Tarja Halonen", R.mipmap.ic_launcher_round)
        appendToList("Android news", "Blip blopp", R.mipmap.ic_launcher_round)
    }

    private fun appendToList(title: String, description: String, image: Int) {
        titlesList.add(title)
        descList.add(description)
        imagesList.add(image)

    }
}