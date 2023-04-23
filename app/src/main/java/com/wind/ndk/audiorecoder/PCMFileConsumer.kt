package com.wind.ndk.audiorecoder

import java.io.FileOutputStream

class PCMFileConsumer(private val pcmFilePath :String) :AudioRecorder.Consumer{
  
    private var mOutputStream : FileOutputStream?=null
    override fun begin() {
        mOutputStream = FileOutputStream(pcmFilePath)
    }

    override fun consume(data: ByteArray,len:Int) {
        mOutputStream?.write(data,0,len)
    }

    override fun end() {
        mOutputStream?.close()
    }

}