package com.example.coffeeriver

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

private const val TAG = "PlayActivity"
private const val EXTRA_CHANNEL = "com.example.coffeeriver.channel"
private const val EXTRA_CHANNEL_URL = "com.example.coffeeriver.channel_url"
private const val EXTRA_CHANNEL_IMAGE_URL = "com.example.coffeeriver.channel_image_url"

class PlayActivity : AppCompatActivity() {
    private lateinit var playButton: ImageButton
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var volumeBar: SeekBar

    private lateinit var backButton: ImageButton
    private lateinit var pageHeader: TextView

    private lateinit var currentUrl: String
    private lateinit var channelImage: ImageView
    private lateinit var channelImageUrl: String

    private var mpIsRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PlayActivity", "create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        currentUrl = intent.getStringExtra(EXTRA_CHANNEL_URL).toString()
        channelImageUrl = intent.getStringExtra(EXTRA_CHANNEL_IMAGE_URL).toString()

        try {
            initializeStream(currentUrl)
            mpIsRunning = true
        } catch (e: Exception) {
            Log.d(TAG, "Failed to initialize Stream")
            mediaPlayer.release()
            finish()
        }

        try {
            prepareStream()
        } catch (e: Exception) {
            Log.d(TAG, "Failed to prepare Stream")
            mediaPlayer.release()
            mpIsRunning = false
            finish()
        }

        buildView()
    }

    companion object {
        fun newIntent(packageContext: Context, channel: String, channelUrl: String, channelImageUrl: String): Intent {
            return Intent(packageContext, PlayActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL, channel)
                putExtra(EXTRA_CHANNEL_URL, channelUrl)
                putExtra(EXTRA_CHANNEL_IMAGE_URL, channelImageUrl)
            }
        }
    }

    private fun buildView() {
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            mediaPlayer.release()
            finish()
        }

        pageHeader = findViewById(R.id.page_header)
        pageHeader.setText(intent.getStringExtra(EXTRA_CHANNEL))

        channelImage = findViewById(R.id.channel_image)
        Picasso.with(this).load(channelImageUrl).placeholder(R.drawable.ic_sharp_image_24).into(channelImage)

        volumeBar = findViewById(R.id.volume_bar)
        volumeBar.setOnSeekBarChangeListener(
                object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if(fromUser) {
                            var volumeValue = progress / 100.0f
                            mediaPlayer.setVolume(volumeValue, volumeValue)
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                }
        )

        playButton = findViewById(R.id.play_button)
        playButton.setOnClickListener {
            playBtnClick()
        }
    }

    private fun initializeStream(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(url)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    private fun prepareStream() {
        try {
            mediaPlayer.prepareAsync()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
    }

     private fun playBtnClick() {
        if (mediaPlayer.isPlaying) {
            playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
            mediaPlayer.pause()
            Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show()
        } else {
            playButton.setImageResource(R.drawable.ic_outline_pause_circle_filled_24)
            mediaPlayer.start()
            Toast.makeText(this, "Resuming", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "back")
        mediaPlayer.release()
        mpIsRunning = false
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "pause")
        mediaPlayer.release()
        mpIsRunning = false
    }

    override fun onResume() {
        super.onResume()
        if(!mpIsRunning) {
            initializeStream(currentUrl)
            prepareStream()
        }
        Log.d(TAG, "resume")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stop")
        mediaPlayer.release()
        mpIsRunning = false
    }
}