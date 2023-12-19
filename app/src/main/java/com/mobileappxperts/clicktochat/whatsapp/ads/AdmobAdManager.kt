package com.example.notisave.ads

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.gson.Gson

object AdmobAdManager {

    private var appId = ""
    private var interstitialId = ""
    private var bannerId = ""
    private var nativeId = ""
    private var appOpenId = ""

    var interstitialAd: InterstitialAd? = null
    var nativeAd: NativeAd? = null

    fun init(jsonString: String, application: Application) {
        val gson = Gson()
        val adModel = gson.fromJson(jsonString, AdModel::class.java)
        appId = adModel.appId
        interstitialId = adModel.interstitialId
        bannerId = adModel.bannerId
        nativeId = adModel.nativeId
        appOpenId = adModel.appOpenId
        setAppId(application, application)
    }

    private fun setAppId(context: Context, application: Application) {
        try {
            val ai = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val bundle = ai.metaData
            val myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.d("TAG000", "Name Found: $myApiKey")
            ai.metaData.putString(
                "com.google.android.gms.ads.APPLICATION_ID",
                appId
            )
//            AppOpenAdManager(application, appOpenId)
            Handler(Looper.getMainLooper()).postDelayed({
                /*loadIntertitialAd(context)*/
            }, 1000)
        } catch (e: Exception) {
            Log.e("TAG000", "Failed to load meta-data, NameNotFound: " + e.message)
        }
    }


    /*@SuppressLint("VisibleForTests")
    private fun loadIntertitialAd(context: Context) {
        if (interstitialId.isEmpty() || interstitialAd != null) return
        InterstitialAd.load(
            context,
            interstitialId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("TAG", adError.message)
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("TAG", "Ad was loaded.")
                    interstitialAd = ad
                }
            }
        )
    }*/

    @SuppressLint("VisibleForTests")
    fun Context.loadBannerAd(frameLayout: FrameLayout) {
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = bannerId
        frameLayout.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("TAG11111", "onAdFailedToLoad: " + p0.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e("TAG11111", "onAdLoaded: ")
            }
        }
    }



  /*  fun loadAndShowNativeAd(context: Context, frameLayout: FrameLayout) {
        if (nativeAd != null) {
            setSmallNativeAd(context, frameLayout)
        } else {
            val builder: AdLoader.Builder?
            if (isNetworkAvailable(context)) {
                builder = AdLoader.Builder(context, nativeId)
                builder.forNativeAd(OnNativeAdLoadedListener { unifiedNativeAd: NativeAd ->
                    nativeAd = unifiedNativeAd
                    setSmallNativeAd(context, frameLayout)
                })
                builder.withAdListener(object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        Log.e("TAG1112", "onAdClosed: ")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.e("TAG1112", "onAdFailedToLoadNative:" + loadAdError.code)
                        Log.e("TAG1112", "onAdFailedToLoadNative:" + loadAdError.message)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.e("TAG1112", "onAdLoaded: ")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        nativeAd = null
                        loadAndShowNativeAd(context, frameLayout)
                        Log.e("TAG1112", "onAdOpened: ")
                    }
                })
                val videoOptions = VideoOptions.Builder()
                    .setStartMuted(true)
                    .build()
                val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
                builder.withNativeAdOptions(adOptions)
                val adLoader = builder.build()
                adLoader.loadAd(AdRequest.Builder().build())

            }
        }
    }

    private fun setSmallNativeAd(
        context: Context,
        frameLayout: FrameLayout?,
        isShowMedia: Boolean = false,
    ) {
        val inflater = LayoutInflater.from(context)
        val adView: NativeAdView =
            inflater.inflate(R.layout.layout_small_native_ad_mob, null) as NativeAdView
        if (frameLayout != null) {
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
            frameLayout.visibility = View.VISIBLE
        }
        try {
            if (isShowMedia) {
                val mediaView = adView.findViewById<MediaView>(R.id.mediaView)
                nativeAd?.mediaContent?.let { mediaView.mediaContent = it }
                mediaView.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                    override fun onChildViewAdded(parent: View, child: View) {
                        if (child is ImageView) {
                            child.adjustViewBounds = true
                            child.scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    }

                    override fun onChildViewRemoved(parent: View, child: View) {}
                })
                adView.mediaView = mediaView
            }
            adView.headlineView = adView.findViewById(R.id.adTitle)
            adView.bodyView = adView.findViewById(R.id.adDescription)
            adView.iconView = adView.findViewById(R.id.adIcon)
            adView.advertiserView = adView.findViewById(R.id.adAdvertiser)
            adView.callToActionView = adView.findViewById(R.id.callToAction)
            (adView.headlineView as TextView).text = nativeAd?.headline
            if (nativeAd?.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text =
                    nativeAd?.body *//*"\t\t\t" + nativeAd.body*//*
            }
            if (nativeAd?.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd?.callToAction
            }
            if (nativeAd?.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(nativeAd?.icon?.drawable)
                adView.iconView?.visibility = View.VISIBLE
            }
            if (isShowMedia) {
                adView.mediaView?.visibility = View.VISIBLE
            } else {
                adView.mediaView?.visibility = View.GONE
            }
            if (nativeAd?.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd?.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }

            val vc = nativeAd?.mediaContent?.videoController
            vc?.mute(true)
            if (vc?.hasVideoContent() == true) {
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {}
            }
            nativeAd?.let { adView.setNativeAd(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "populateUnifiedNativeAdView Exception: " + e.message)
        }
    }

    private fun setBigNativeAd(
        context: Context,
        frameLayout: FrameLayout?,
        isShowMedia: Boolean = true,
    ) {
        val inflater = LayoutInflater.from(context)
        val adView: NativeAdView =
            inflater.inflate(R.layout.layout_big_native_ad_mob, null) as NativeAdView
        if (frameLayout != null) {
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
            frameLayout.visibility = View.VISIBLE
        }
        try {
            if (isShowMedia) {
                val mediaView = adView.findViewById<MediaView>(R.id.mediaView)
                nativeAd?.mediaContent?.let { mediaView.mediaContent = it }
                mediaView.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                    override fun onChildViewAdded(parent: View, child: View) {
                        if (child is ImageView) {
                            child.adjustViewBounds = true
                            child.scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    }

                    override fun onChildViewRemoved(parent: View, child: View) {}
                })
                adView.mediaView = mediaView
            }
            adView.headlineView = adView.findViewById(R.id.adTitle)
            adView.bodyView = adView.findViewById(R.id.adDescription)
            adView.iconView = adView.findViewById(R.id.adIcon)
            adView.advertiserView = adView.findViewById(R.id.adAdvertiser)
            adView.callToActionView = adView.findViewById(R.id.callToAction)
            (adView.headlineView as TextView).text = nativeAd?.headline
            if (nativeAd?.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text =
                    nativeAd?.body *//*"\t\t\t" + nativeAd.body*//*
            }
            if (nativeAd?.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd?.callToAction
            }
            if (nativeAd?.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(nativeAd?.icon?.drawable)
                adView.iconView?.visibility = View.VISIBLE
            }
            if (isShowMedia) {
                adView.mediaView?.visibility = View.VISIBLE
            } else {
                adView.mediaView?.visibility = View.GONE
            }
            if (nativeAd?.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd?.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }

            val vc = nativeAd?.mediaContent?.videoController
            vc?.mute(true)
            if (vc?.hasVideoContent() == true) {
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {}
            }
            nativeAd?.let { adView.setNativeAd(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "populateUnifiedNativeAdView Exception: " + e.message)
        }
    }

    private fun isNetworkAvailable(c: Context): Boolean {
        val manager = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        var isAvailable = false
        if (networkInfo != null && networkInfo.isConnected) {
            isAvailable = true
        }
        return isAvailable
    }*/
}