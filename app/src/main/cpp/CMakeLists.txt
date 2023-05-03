# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.22.1)

project("audiorecoder")


set(THIRD_PARTY "${CMAKE_SOURCE_DIR}/thirdparty")
set(FFMPEG_LIBS "${THIRD_PARTY}/prebuilt/${CMAKE_ANDROID_ARCH_ABI}/")
#链接ffmpeng
set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -L${FFMPEG_LIBS}")

#引入头文件
include_directories(${THIRD_PARTY}/ffmpeg/include/)

add_library(
        audiorecoder
        SHARED

        com_wind_ndk_audiorecoder_AudioEncoder.cpp
        audio_encoder.cpp
)



target_link_libraries(
        audiorecoder

        android
        fdk-aac
        x264
        avcodec
        avfilter
        avformat
        avutil
        swresample
        swscale

        # 忽略顺序的方式，导入
       # -Wl,--start-group
       # avcodec avfilter avformat avutil swresample swscale
      #  -Wl,--end-group

        z
        log)