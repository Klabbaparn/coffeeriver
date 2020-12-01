package com.example.coffeeriver

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import safety.com.br.android_shake_detector.core.ShakeDetector
import safety.com.br.android_shake_detector.core.ShakeOptions


class ChannelActivity : AppCompatActivity() {

    private lateinit var channelLayout: LinearLayout
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        channelLayout = findViewById(R.id.channel_layout)

        fun createButton(channel: JSONObject) {
            val dynamicButton = Button(this)
            val buttonLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            buttonLayoutParams.setMargins(30,30,30,0)
            dynamicButton.setLayoutParams(buttonLayoutParams)
            dynamicButton.text = channel.getString("name")
            dynamicButton.setBackgroundColor(Color.WHITE)
            dynamicButton.setTextColor(Color.BLACK)
            dynamicButton.setOnClickListener { Toast.makeText(this, "Clicked button", Toast.LENGTH_SHORT).show() }
            channelLayout.addView(dynamicButton)
        }

        Fuel
                .get("https://api.sr.se/api/v2/channels?format=json")
                .responseString{ _, _, result ->
                    val obj = JSONObject(result.get())
                    val channels = obj.getJSONArray("channels")
                    for (i in 0 until channels.length()) {
                        val channel = channels.getJSONObject(i)
                        createButton(channel)
                    }
                }

        //Shaker
        val options = ShakeOptions()
            .background(true)
            .interval(1000)
            .shakeCount(2)
            .sensibility(2.0f)
        shakeDetector = ShakeDetector(options).start(
            this
        ) { Toast.makeText(app,"this is toast message",Toast.LENGTH_SHORT).show()  }
    }
}