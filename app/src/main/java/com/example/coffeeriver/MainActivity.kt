package com.example.coffeeriver


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.rotationMatrix
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.station_item.*
import kotlinx.android.synthetic.main.station_item.view.*

class MainActivity : AppCompatActivity(), StationAdapter.OnStationClickListener {
    private var stationList = mutableListOf<StationItem>()
    private lateinit var sharedPreferences: SharedPreferences
    private val channelID = "Radio Horizon"
    private val notificationID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)
        initData()
        createNotificationChannel()

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
            sendNotification("Favorite removed", "${stationList[position].title} was removed from the favorite list", stationList[position].imageResource)
        }
        else {
            editor.putBoolean("STATION_ID_$position", true)
            stationList[position].ImageButton = R.drawable.ic_favorite_red_45
            sendNotification("Favorite added", "${stationList[position].title} was added to the favorite list", stationList[position].imageResource)
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
            stationList.add(StationItem(R.drawable.bandit_rock_icon, "Bandit Rock", isFavorite("STATION_ID_1")))
            stationList.add(StationItem(R.drawable.finska_icon, "Finish Radio", isFavorite("STATION_ID_2")))
            stationList.add(StationItem(R.drawable.rix_fm_icon, "Rix FM", isFavorite("STATION_ID_3")))
        }
        else {
            stationList.add(StationItem(R.drawable.sr_icon_1, "Sveriges Radio", R.drawable.ic_favorite_red_45))
            stationList.add(StationItem(R.drawable.bandit_rock_icon, "Bandit Rock", R.drawable.ic_favorite_shadow_45))
            stationList.add(StationItem(R.drawable.finska_icon, "Finish Radio", R.drawable.ic_favorite_red_45))
            stationList.add(StationItem(R.drawable.rix_fm_icon, "Rix FM", R.drawable.ic_favorite_shadow_45))
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(notificationTitle: String, notificationMsg: String, icon: Int) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, icon)
        val bitmapLarge = BitmapFactory.decodeResource(applicationContext.resources, icon)

        val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMsg)
                .setLargeIcon(bitmapLarge)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationID, builder.build())
        }
    }

}