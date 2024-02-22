package openinwhatsapp.directchat.clicktochat

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.nirav.commons.ads.CommonAdManager

class App : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        MobileAds.initialize(this) {}
        Log.i("TAG", "onCreatesdfgsdfg1: 1")

    }

    fun getAdsFromRemoteConfig(activity: Activity, onAdsInitialized: () -> Unit) {
        FirebaseApp.initializeApp(this)

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10000).build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        val jsonConfigKey = if (BuildConfig.DEBUG) "test_ids" else "real_ids"

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val json = remoteConfig.getString(jsonConfigKey)
                    CommonAdManager.init(
                        activity = activity,
                        application = this,
                        jsonString = json,
                        onAdsInitialized = onAdsInitialized
                    )
                } else {
                    Log.e("TAG", "Error occurred")
                }
            }
    }

}