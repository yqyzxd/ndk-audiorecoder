package com.wind.ndk.audiorecoder

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

object AudioRecorder {
    private const val SAMPLE_RATE_IN_HZ_44100=44100
    private const val SAMPLE_RATE_IN_HZ_16000=16000
    private const val CHANNEL_CONFIGURATION=AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT=AudioFormat.ENCODING_PCM_16BIT
    private const val AUDIO_SOURCE=MediaRecorder.AudioSource.MIC

    private var mAudioRecord :AudioRecord?=null
    private var mBufferSizeInBytes=0
    private var mRecording = false
    private var mRecordThread:Thread?=null
    private var mConsumer:Consumer?=null

    @Throws(AudioConfigurationException::class,AudioRecordPermissionDeniedException::class)
    fun configure(context:Context){

        if (ActivityCompat.checkSelfPermission(context,android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            throw AudioRecordPermissionDeniedException()
        }

        mAudioRecord?.release()

        val sampleRateInHzs= listOf(SAMPLE_RATE_IN_HZ_44100,SAMPLE_RATE_IN_HZ_16000)

        for (sampleRateInHz in sampleRateInHzs){
            try {
                mBufferSizeInBytes=AudioRecord.getMinBufferSize(sampleRateInHz,CHANNEL_CONFIGURATION,AUDIO_FORMAT)
                mAudioRecord= AudioRecord(AUDIO_SOURCE, sampleRateInHz, CHANNEL_CONFIGURATION,AUDIO_FORMAT,mBufferSizeInBytes)

            }catch (e:Exception){
                e.printStackTrace()
            }

            if (mAudioRecord?.state == AudioRecord.STATE_INITIALIZED){

                break
            }

            //创建AudioRecord失败，更换采样率重试
        }

        if (mAudioRecord?.state != AudioRecord.STATE_INITIALIZED){
            //还是失败
            throw AudioConfigurationException()
        }

    }


    fun start(){
        if (mAudioRecord?.state == AudioRecord.STATE_INITIALIZED){
            mAudioRecord?.startRecording()

            mRecording=true

            mRecordThread= Thread(RecordRunnable())
            mRecordThread?.start()
        }
    }

    fun stop(){
        mRecording=false
        mRecordThread?.join()
        //release里面已经调用了stop方法
        mAudioRecord?.release()
        mAudioRecord=null

    }

    private class RecordRunnable:Runnable{
        override fun run() {
           val samples= ByteArray(mBufferSizeInBytes)
            mConsumer?.begin()
            while (mRecording){
                val audioSampleSize= mAudioRecord?.read(samples,0, mBufferSizeInBytes)?:0
                if (audioSampleSize>0){
                    mConsumer?.consume(samples,audioSampleSize)
                }
            }
            mConsumer?.end()

        }

    }

    fun setConsumer(consumer: Consumer){
        this.mConsumer=consumer
    }
    interface Consumer{
        fun begin()
        fun consume(byteArray: ByteArray,len:Int)
        fun end()
    }

}