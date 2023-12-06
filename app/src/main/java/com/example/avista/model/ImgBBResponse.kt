package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class ImgBBResponse(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)

data class ImgBBData(
    val id: String,
    val title: String,
    @SerializedName("url_viewer")
    val urlViewer: String,
    val url: String,
    @SerializedName("display_url")
    val displayUrl: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val time: Long,
    val expiration: Int,
    val image: ImgBBImage,
    val thumb: ImgBBImage,
    @SerializedName("delete_url")
    val deleteUrl: String
)

data class ImgBBImage(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)
