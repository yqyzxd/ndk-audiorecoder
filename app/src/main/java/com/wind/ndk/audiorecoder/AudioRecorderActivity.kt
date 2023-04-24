package com.wind.ndk.audiorecoder

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wind.ndk.audiorecoder.databinding.ActivityAudioRecorderBinding

class AudioRecorderActivity :AppCompatActivity(){
    private lateinit var binding: ActivityAudioRecorderBinding
    private var mRecording=false
    private val mAudioRecorder=AudioRecorder
    private var mRecordingTimeInSec=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val outputPath =   getExternalFilesDir(null)?.absolutePath + "/vocal.pcm"
        mAudioRecorder.setConsumer(PCMFileConsumer(outputPath))
        binding.btnRecord.setOnClickListener{
            if (mRecording){
                mRecording=false
                binding.btnRecord.text = "record"
                val time = String.format("%02d:%02d:%02d",0, 0, 0)
                binding.tvTime.text=time
                mRecordingTimeInSec=0
                mAudioRecorder.stop()
                mHandler.removeCallbacksAndMessages(null)
            }else{
                if (ActivityCompat.checkSelfPermission(it.context,android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(it.context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(it.context as Activity, arrayOf(android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
                    return@setOnClickListener
                }
                binding.btnRecord.text = "stop"
                mRecording=true
                mAudioRecorder.configure(context=it.context)
                mAudioRecorder.start()

                mHandler.sendEmptyMessageDelayed(0,1000)
            }
        }
    }

    private val mHandler=object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            mRecordingTimeInSec++
            val hours=mRecordingTimeInSec/3600
            val remainSec=mRecordingTimeInSec%3600
            val mins=remainSec/60
            val secs=remainSec%60

            val time = String.format("%02d:%02d:%02d",hours, mins, secs)
            binding.tvTime.text=time
            sendEmptyMessageDelayed(0,1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAudioRecorder.stop()
        mHandler.removeCallbacksAndMessages(null)
    }
}