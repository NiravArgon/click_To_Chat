package com.example.notisave.ads

import com.google.gson.annotations.SerializedName

data class AdModel(
    @SerializedName("app_id") val appId: String,
    @SerializedName("interstitial_id") val interstitialId: String,
    @SerializedName("banner_id") val bannerId: String,
    @SerializedName("native_id") val nativeId: String,
    @SerializedName("app_open_id") val appOpenId: String
)