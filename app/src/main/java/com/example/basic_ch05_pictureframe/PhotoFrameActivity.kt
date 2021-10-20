package com.example.basic_ch05_pictureframe

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.basic_ch05_pictureframe.databinding.ActivityPhotoFrameBinding
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {

    private lateinit var PhotoFrameBinding : ActivityPhotoFrameBinding

    private val photoList = mutableListOf<Uri>()    // 전달 받은 Uri를 저장할 리스트

    private var currentPosition = 0

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        PhotoFrameBinding = ActivityPhotoFrameBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(PhotoFrameBinding.root)

        getPhotoUriFromIntent()
        // startTimer()    최초 한번만 실행하기 때문에 onStart()로 옮겨준다

    }

    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize",0)
        for (i in 0..size) {
            // null이 아닐때만 실행
            intent.getStringExtra("Photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer() {
        timer = timer(period = 5 * 1000) {
            runOnUiThread {
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                PhotoFrameBinding.backgroundPhotoImageView.setImageURI(photoList[current])

                PhotoFrameBinding.photoImageView.alpha = 0f    // alpha : 투명도 / 0f -> 투명하게 된다
                PhotoFrameBinding.photoImageView.setImageURI(photoList[next])
                PhotoFrameBinding.photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next

            }
        }
    }

    override fun onStop() {
        super.onStop()

        timer?.cancel()

    }

    override fun onStart() {
        super.onStart()

        startTimer()

    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
    }









}