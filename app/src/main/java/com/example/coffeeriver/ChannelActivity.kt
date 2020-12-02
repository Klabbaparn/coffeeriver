package com.example.coffeeriver

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import org.json.JSONArray
import org.json.JSONObject
import safety.com.br.android_shake_detector.core.ShakeCallback
import safety.com.br.android_shake_detector.core.ShakeDetector
import safety.com.br.android_shake_detector.core.ShakeOptions
import kotlin.random.Random

private const val EXTRA_STATION = "com.example.coffeeriver.station"

class ChannelActivity : AppCompatActivity() {

    private lateinit var channelLayout: LinearLayout
    private lateinit var backButton: ImageButton
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var channels: JSONArray
    private lateinit var randomChannel: JSONObject
    private lateinit var pageHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        channelLayout = findViewById(R.id.channel_layout)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        pageHeader = findViewById(R.id.page_header)
        Toast.makeText(this,"Picked Station: " + intent.getIntExtra(EXTRA_STATION, 0).toString() + ". Wow!", Toast.LENGTH_LONG).show()

        fun createButton(channel: JSONObject) {
            val dynamicButton = Button(this)
            val buttonLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            buttonLayoutParams.setMargins(30,30,30,0)
            dynamicButton.setLayoutParams(buttonLayoutParams)
            dynamicButton.text = channel.getString("name")
            dynamicButton.setBackgroundColor(Color.WHITE)
            dynamicButton.setTextColor(Color.BLACK)
            dynamicButton.setOnClickListener {
                val intent = PlayActivity.newIntent(this@ChannelActivity,
                        channel.getString("name"),
                        channel.getJSONObject("liveaudio").getString("url"),
                        channel.getString("image")
                )
                startActivity(intent)
            }

            channelLayout.addView(dynamicButton)
        }

        Fuel
                .get("https://api.sr.se/api/v2/channels?format=json")
                .responseString{ _, _, result ->
                    val obj = JSONObject(result.get())
                    channels = obj.getJSONArray("channels")
                    for (i in 0 until channels.length()) {
                        val channel = channels.getJSONObject(i)
                        createButton(channel)
                    }
                }

        //Shaker
        val options = ShakeOptions()
            .background(false)
            .interval(1000)
            .shakeCount(2)
            .sensibility(2.0f)

        shakeDetector = ShakeDetector(options).start(this) {
            shakeDetector.stopShakeDetector(baseContext)
            randomChannel = channels.get(Random.nextInt(0, channels.length())) as JSONObject
            val intent = PlayActivity.newIntent(this@ChannelActivity,
                    randomChannel.getString("name"),
                    randomChannel.getJSONObject("liveaudio").getString("url"),
                    randomChannel.getString("image")
            )
            startActivity(intent)
        }
    }

    companion object {
        fun newIntent(packageContext: Context, station: Int): Intent {
            return Intent(packageContext, ChannelActivity::class.java).apply {
                putExtra(EXTRA_STATION, station)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start(baseContext)
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stopShakeDetector(baseContext)
    }

    override fun onDestroy() {
        shakeDetector.destroy(baseContext)
        super.onDestroy()
    }
}