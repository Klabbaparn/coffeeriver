package com.example.coffeeriver

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.squareup.picasso.Picasso
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.math.log

class PlayActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var volumeBar: SeekBar
    private lateinit var channelImage: ImageView
    private var currentUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PlayActivity", "create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        currentUrl = "https://sverigesradio.se/topsy/direkt/srapi/213.mp3"
        var currentChannel = "P1"
        var channelImageUrl = "https://static-cdn.sr.se/images/132/2186745_512_512.jpg?preset=api-default-square"

        channelImage = findViewById(R.id.channel_image)
        Picasso.with(this).load(channelImageUrl).placeholder(R.drawable.ic_sharp_image_24).into(channelImage)

        initializeStream(currentUrl)
        prepareStream()

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

    override fun onResume() {
        super.onResume()
        initializeStream(currentUrl)
        prepareStream()
        Log.d("PlayActivity", "resume")
    }

    override fun onStop() {
        super.onStop()
        Log.d("PlayActivity", "stop")
        mediaPlayer.release()
    }
}