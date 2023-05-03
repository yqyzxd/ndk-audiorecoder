package com.wind.ndk.audiorecoder

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wind.ndk.audiorecoder.databinding.ActivityAudioEncoderBinding

class AudioEncoderActivity  :AppCompatActivity() {
    private lateinit var binding: ActivityAudioEncoderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioEncoderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pcmPath =   getExternalFilesDir(null)?.absolutePath + "/vocal.pcm"
        val aacPath =   getExternalFilesDir(null)?.absolutePath + "/aac.aac"

        binding.btnEncode.setOnClickListener {
            val startTimeMills = System.currentTimeMillis()
            val audioEncoder = AudioEncoder()
            audioEncoder.encode(pcmPath, 1, 128 * 1024, 44100, aacPath)
            val wasteTimeMills = (System.currentTimeMillis() - startTimeMills).toInt()
            Log.i("success", "wasteTimeMills is : $wasteTimeMills")
        }
    }
}