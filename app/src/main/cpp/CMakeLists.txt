# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.22.1)

project("audiorecoder")


set(THIRD_PARTY "${CMAKE_SOURCE_DIR}/thirdparty")

set(FFMPEG_LIBS "${THIRD_PARTY}/prebuilt/${ANDROID_ABI}")

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
        ${FFMPEG_LIBS}/libavfilter.a
        ${FFMPEG_LIBS}/libavformat.a
        ${FFMPEG_LIBS}/libavcodec.a
        ${FFMPEG_LIBS}/libswresample.a
        ${FFMPEG_LIBS}/libswscale.a
        ${FFMPEG_LIBS}/libavutil.a
        ${FFMPEG_LIBS}/libfdk-aac.a
        ${FFMPEG_LIBS}/libx264.a
        z
        log)