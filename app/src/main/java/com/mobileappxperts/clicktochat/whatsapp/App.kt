package com.mobileappxperts.clicktochat.whatsapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.notisave.ads.AdmobAdManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class App : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        MobileAds.initialize(this) {}
        Log.i("TAG", "onCreatesdfgsdfg1: 1")
       /* SharedPrefs.init(this)



        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, getString(R.string.one_signal_id))*/
        Thread {
            getDataFromRemoteConfig()
        }.start()
    }


    private fun getDataFromRemoteConfig() {
        FirebaseApp.initializeApp(this)

        Log.i("TAG", "onCreatesdfgsdfg1: 2")

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0)
                .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        val jsonConfigKey = if (BuildConfig.DEBUG) "test_ids" else "real_ids"
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val json = remoteConfig.getString(jsonConfigKey)
                    AdmobAdManager.init(jsonString = json, application = this)

                    Log.i("TAG", "onCreatesdfgsdfg1: 3")


//                    val jsonArray = JSONArray(json)
//                    for (i in 0 until jsonArray.length()) {
//                        val jsonObject = jsonArray.getJSONObject(i)
//                        val adType = jsonObject.getString("adstype")
//                        val adId = jsonObject.getString("adsid")
//
//                        when (adType) {
//                            "app_id" -> Constants.app_id = adId
//                            "app_open_id" -> Constants.app_open_id = adId
//                            "banner_id" -> Constants.banner_id = adId
//                            "interstitial_id" -> Constants.interstitial_id = adId
//                            "native_id" -> Constants.native_id = adId
//                        }
//                        setAppId()
//                        //app open ads init
////                        AppOpenAdManager(this, Constants.app_open_id)
//                    }

//                    Constants.adInterval = FirebaseRemoteConfig.getInstance().getLong("ad_interval")
                } else {
                    // Handle the error
                }
            }
    }

}