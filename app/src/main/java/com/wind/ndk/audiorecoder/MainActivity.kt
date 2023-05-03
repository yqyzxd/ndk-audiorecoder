package com.wind.ndk.audiorecoder

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.wind.ndk.audiorecoder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btn.setOnClickListener {
            startActivity(Intent(it.context,AudioRecorderActivity::class.java))
        }
        binding.btnEncode.setOnClickListener {
            startActivity(Intent(it.context,AudioEncoderActivity::class.java))
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),1)
        }
    }

}