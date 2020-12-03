package com.example.coffeeriver

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
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

    private lateinit var channelImage: ImageView

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

        if(intent.getIntExtra(EXTRA_STATION, 0) == 0) {
            fun createButton(channel: JSONObject) {
                /*val dynamicButton = Button(this)
                val buttonLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                buttonLayoutParams.setMargins(30, 30, 30, 0)
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
                channelLayout.addView(dynamicButton)*/

                /*val dynamicScrollView: ScrollView = ScrollView(this)
                val svLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dynamicScrollView.setLayoutParams(svLayoutParams)*/

                val dynamicScrollView = ScrollView(this)
                val svLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dynamicScrollView.layoutParams = svLayoutParams

                val dynamicLinearLayout: LinearLayout = LinearLayout(this)
                val llLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                llLayoutParams.setMargins(50, 20, 50, 0)
                dynamicLinearLayout.setLayoutParams(llLayoutParams)
                dynamicLinearLayout.setBackgroundColor(Color.WHITE)

                dynamicScrollView.addView(dynamicLinearLayout)

                val dynamicImageView: ImageView = ImageView(this)
                val ivLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dynamicImageView.setLayoutParams(ivLayoutParams)
                dynamicImageView.setPadding(50, 20, 0,20)
                val channelImageUrl: String = channel.getString("image")
                Picasso.with(this).load(channelImageUrl).resize(100, 100).into(dynamicImageView)
                dynamicLinearLayout.addView(dynamicImageView)

                val dynamicTextView: TextView = TextView(this)
                val tvLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dynamicImageView.setLayoutParams(tvLayoutParams)
                dynamicTextView.setPadding(100, 45, 0, 0)
                dynamicTextView.text = channel.getString("name")
                dynamicTextView.textSize = 20f
                dynamicTextView.setTextColor(Color.BLACK)
                dynamicLinearLayout.addView(dynamicTextView)

                dynamicLinearLayout.setOnClickListener {
                    val intent = PlayActivity.newIntent(this@ChannelActivity,
                            channel.getString("name"),
                            channel.getJSONObject("liveaudio").getString("url"),
                            channel.getString("image")
                    )
                    startActivity(intent)
                }

                channelLayout.addView(dynamicScrollView)
            }

            Fuel
                    .get("https://api.sr.se/api/v2/channels?format=json")
                    .responseString { _, _, result ->
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
        } else {
            val tv_dynamic = TextView(this)
            tv_dynamic.textSize = 20f
            val tv_dynamicLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tv_dynamicLayoutParams.setMargins(30, 30, 30, 0)
            tv_dynamic.setLayoutParams(tv_dynamicLayoutParams)
            tv_dynamic.text = getString(R.string.not_implemented)
            tv_dynamic.setTextColor(Color.BLACK)
            channelLayout.addView(tv_dynamic)
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
        if(intent.getIntExtra(EXTRA_STATION, 0) == 0) {
            shakeDetector.start(baseContext)
        }
    }

    override fun onPause() {
        super.onPause()
        if(intent.getIntExtra(EXTRA_STATION, 0) == 0) {
            shakeDetector.stopShakeDetector(baseContext)
        }
    }

    override fun onDestroy() {
        if(intent.getIntExtra(EXTRA_STATION, 0) == 0) {
            shakeDetector.destroy(baseContext)
        }
        super.onDestroy()
    }
}