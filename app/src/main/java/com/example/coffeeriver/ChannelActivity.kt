package com.example.coffeeriver

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject

class ChannelActivity : AppCompatActivity() {

    private lateinit var channelLayout: LinearLayout
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        channelLayout = findViewById(R.id.channel_layout)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        fun createButton(channel: JSONObject) {
            val dynamicButton = Button(this)
            val buttonLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            buttonLayoutParams.setMargins(30,30,30,0)
            dynamicButton.setLayoutParams(buttonLayoutParams)
            dynamicButton.text = channel.getString("name")
            dynamicButton.setBackgroundColor(Color.WHITE)
            dynamicButton.setTextColor(Color.BLACK)
            dynamicButton.setOnClickListener {
                Toast.makeText(this, "Clicked button", Toast.LENGTH_SHORT).show()
                val channel = "P1"
                val channelUrl = "https://sverigesradio.se/topsy/direkt/srapi/213.mp3"
                val channelImageUrl = "https://static-cdn.sr.se/images/132/2186745_512_512.jpg?preset=api-default-square"
                val intent = PlayActivity.newIntent(this@ChannelActivity, channel, channelUrl, channelImageUrl)
                startActivity(intent)
            }

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
    }
}