package com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.utils

import AppPreferences
import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.play.core.review.ReviewManagerFactory
import com.mobileappxperts.clicktochat.whatsapp.R
import com.mobileappxperts.clicktochat.whatsapp.databinding.CustomSettingPopupLayoutBinding
import com.mobileappxperts.clicktochat.whatsapp.databinding.MainLayoutBinding
import java.net.URLEncoder


object Constant {
    const val REQUEST_CODE_READ_CONTACTS = 100
    var isAnimation: Boolean = false

    fun openWhatsAppChat(phoneNumber: String, context: Context) {
        try {
            val uri = Uri.parse("https://wa.me/$phoneNumber")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun openWhatsAppChatWithChat(
        phoneNumber: String,
        message: String? = null,
        context: Context,
    ) {
        try {
            val uri = Uri.parse(
                "https://api.whatsapp.com/send?phone=$phoneNumber" +
                        if (!message.isNullOrBlank()) "&text=${
                            URLEncoder.encode(
                                message,
                                "UTF-8"
                            )
                        }" else ""
            )
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun openTelegramChatByPhoneNumber(phoneNumber: String, context: Context) {
        try {
//            val internationalPhoneNumber = phoneNumber.replace("+", "").replace(" ", "")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$phoneNumber"))
            intent.setPackage("org.telegram.messenger")

            if (intent.resolveActivity(context.packageManager) == null) {
                // Telegram not installed, open website
                Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + phoneNumber)).also {
                    context.startActivity(it)
                    Log.i("TAG", "openTelegramChatByPhoneNumber: " + "https://t.me/" + phoneNumber)
                }
                return
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_CODE_READ_CONTACTS
                )
                return
            }

            Log.i("TAG", "openTelegramChatByPhoneNumber: " + intent)
            context.startActivity(intent)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun openTelegramChatWithMessage(
        phoneNumber: String,
        message: String? = null,
        context: Context,
    ) {
        try {
            /*  val internationalPhoneNumber = phoneNumber.replace("+", "").replace(" ", "")
  */
            val uri = Uri.parse(
                "https://t.me/$phoneNumber" +
                        if (!message.isNullOrBlank()) "?text=${
                            URLEncoder.encode(
                                message,
                                "UTF-8"
                            )
                        }" else ""
            )

            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Telegram is not installed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun openSignalChat(
        phoneNumber: String,
        context: Context,
    ) {
        try {
            val internationalPhoneNumber = phoneNumber.replace("+", "").replace(" ", "")
            val intent = Intent(Intent.ACTION_VIEW)
            if (intent.resolveActivity(context.packageManager) != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.data = Uri.parse("smsto:$internationalPhoneNumber")
                    intent.setPackage("org.thoughtcrime.securesms")
                } else {
                    intent.data = Uri.parse("sms:$internationalPhoneNumber")
                    intent.putExtra("address", internationalPhoneNumber)
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Signal is not installed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun openSignalChatWithMessage(
        phoneNumber: String,
        message: String? = null,
        context: Context,
    ) {
        try {
            val internationalPhoneNumber = phoneNumber.replace("+", "").replace(" ", "")

            val uri: Uri = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Uri.parse("smsto:$internationalPhoneNumber")
                } else {
                    Uri.parse("sms:$internationalPhoneNumber")
                }
            } catch (e: Exception) {
                Uri.parse("https://signal.me/$internationalPhoneNumber")
            }

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("org.thoughtcrime.securesms")

            if (intent.resolveActivity(context.packageManager) == null) {
                intent.data = Uri.parse("https://signal.me/$internationalPhoneNumber")
            }

            if (!message.isNullOrEmpty()) {
                intent.putExtra("sms_body", message)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    object AdConstant {
        var isAnimation: Boolean = false
        val rateUsDelay = 10000

        var lastTimeStampForInter: Long = 0
        private var delayForInterstitial = 40000
        fun isAdReadyToShow() =
            (System.currentTimeMillis() - lastTimeStampForInter) > delayForInterstitial

    }


    fun Activity.showRateUsDialog() {
        if ((System.currentTimeMillis() - AppPreferences(this).getstartTime()) < Constant.AdConstant.rateUsDelay) return
        if ((System.currentTimeMillis() - AppPreferences(this).getcheckDaysForReviewDialog()) < 604800000) {
            Log.e("TAGGGGG", "showFeedbackDialog: false")
            return
        }
        Log.e("TAGGGGG", "showFeedbackDialog: true")
        AppPreferences(this).checkDaysForReviewDialog(System.currentTimeMillis())
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        reviewManager.requestReviewFlow().addOnCompleteListener {
            if (it.isSuccessful) {
                reviewManager.launchReviewFlow(this, it.result)
                Log.e("TAG11", "showFeedbackDialog: Successfully Show")
            } else {
                Log.e("TAG11", "showFeedbackDialog: Not Show")
            }
        }
    }


    fun disableButtons(customDialogBinding: MainLayoutBinding, context: Context) {
        val colorStateList = ContextCompat.getColorStateList(context, R.color.button_colors)
        customDialogBinding.btnOpen.apply {
            isEnabled = false
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnCall.apply {
            isEnabled = false
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnShare.apply {
            isEnabled = false
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnSms.apply {
            isEnabled = false
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnShortcut.apply {
            isEnabled = false
            backgroundTintList = colorStateList
        }
    }


    fun enableButtons(customDialogBinding: MainLayoutBinding, context: Context) {
        val colorStateList = ContextCompat.getColorStateList(context, R.color.button_colors)
        customDialogBinding.btnOpen.apply {
            isEnabled = true
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnCall.apply {
            isEnabled = true
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnShare.apply {
            isEnabled = true
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnSms.apply {
            isEnabled = true
            backgroundTintList = colorStateList
        }
        customDialogBinding.btnShortcut.apply {
            isEnabled = true
            backgroundTintList = colorStateList
        }
    }

    fun openSmsWithMessage(phoneNumber: String, message: String, context: Context) {
        try {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra("sms_body", message)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareLink(phoneNumber: String, context: Context) {
        val uri = Uri.parse("https://wa.me/$phoneNumber")
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, uri.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun makePhoneCall(phoneNumber: String, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun clearAppData(context: Context) {
        try {
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                (context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
            } else {
                val packageName = context.applicationContext.packageName
                val runtime = Runtime.getRuntime()
                runtime.exec("pm clear $packageName")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun createWhatsAppChatShortcut(phoneNumber: String, randomId: String, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$phoneNumber")

            val shortcutManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context.getSystemService(ShortcutManager::class.java)
            } else {
                null
            }

            if (shortcutManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && shortcutManager.isRequestPinShortcutSupported) {
                val shortcutInfo = ShortcutInfo.Builder(context, randomId)
                    .setShortLabel(phoneNumber)
                    .setLongLabel(phoneNumber)
                    .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .build()

                shortcutManager.requestPinShortcut(shortcutInfo, null)
            } else {
                val addShortcutIntent = Intent()
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "WhatsApp Chat")
                addShortcutIntent.putExtra(
                    Intent.EXTRA_SHORTCUT_ICON,
                    Icon.createWithResource(context, R.mipmap.ic_launcher)
                )
                addShortcutIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"

                context.sendBroadcast(addShortcutIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


     fun createWhatsAppChatWithMessageShortcut(
        phoneNumber: String,
        message: String? = null,
        randomId: String,
        context: Context
    ) {
        try {
            val uri = Uri.parse(
                "https://api.whatsapp.com/send?phone=$phoneNumber" +
                        if (!message.isNullOrBlank()) "&text=${
                            URLEncoder.encode(
                                message,
                                "UTF-8"
                            )
                        }" else ""
            )

            val intent = Intent(Intent.ACTION_VIEW, uri)

            val shortcutManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context.getSystemService(ShortcutManager::class.java)
            } else {
                null
            }

            var shortcutInfo: ShortcutInfo? = null

            if (shortcutManager != null) {
                val pinShortcutSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    shortcutManager.isRequestPinShortcutSupported
                } else {
                    false
                }

                val addShortcutIntent = Intent()
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
                addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "WhatsApp Chat")
                addShortcutIntent.putExtra(
                    Intent.EXTRA_SHORTCUT_ICON,
                    Icon.createWithResource(context, R.mipmap.ic_launcher)
                )
                addShortcutIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"

                shortcutInfo = if (pinShortcutSupported) {
                    // Use ShortcutManager.Builder for Oreo and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ShortcutInfo.Builder(context, randomId)
                            .setShortLabel(phoneNumber)
                            .setLongLabel(phoneNumber)
                            .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                            .setIntent(intent)
                            .build()
                    } else {
                        null
                    }
                } else {
                    null
                }

                shortcutInfo?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        shortcutManager.requestPinShortcut(it, null)
                    }
                } ?: context.sendBroadcast(addShortcutIntent)
            } else {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


     fun getLastCopiedText(context: Context): String? {
        val appPreferences = AppPreferences(context)
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?

        clipboardManager?.let { manager ->
            if (manager.hasPrimaryClip()) {
                val clip = manager.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val lastCopiedItem = clip.getItemAt(clip.itemCount - 1)
                    val copiedText = lastCopiedItem.text?.toString()

                    if (copiedText != null) {
                        if (appPreferences.getRemoveLeadingZeros()) {
                            val cleanedText = copiedText.removeLeadingZeros()
                            if (isNumeric(cleanedText)) {
                                return cleanedText
                            } else {
                                showToast("Copied text is not a number",context)
                            }
                        } else {
                            if (isNumeric(copiedText)) {
                                return copiedText
                            } else {
                                showToast("Copied text is not a number",context)
                            }
                        }
                    }
                }
            }
        }

        return null
    }

     fun isNumeric(text: String): Boolean {
        return text.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

     fun showToast(message: String,context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun String.removeLeadingZeros(removeZeros: Boolean = true): String {
        return if (removeZeros) {
            this.replaceFirst("^0+".toRegex(), "")
        } else {
            this
        }
    }

    fun getFormattedPhoneNumber(customDialogBinding: MainLayoutBinding): String {
        val countryCode = customDialogBinding.countryCodePicker?.selectedCountryCodeWithPlus
        val carrierNumber = customDialogBinding.editTextCarrierNumber?.text.toString()
        val phoneNumber = "$countryCode$carrierNumber"
        Log.i("TAG", "Formatted phone number: $phoneNumber")
        return phoneNumber
    }




    fun createClickableSpan(context: Context,customDialogBinding: MainLayoutBinding) {
        val completeText =
            context.getString(R.string.this_app_use_whatsapp_public_api_to_open_a_chat_with_any_number_you_enter_no_contact_is_created_on_the_device_n_more_info_here)
        val spannableString = SpannableString(completeText)

        val firstLinkText = "here"
        var start = completeText.indexOf(firstLinkText)
        while (start != -1) {
            val end = start + firstLinkText.length
            spannableString.setSpan(
                createClickableSpan(context,firstLinkText) {
                    startActivity("https://faq.whatsapp.com/general/chats/how-to-use-click-to-chat?lang=en-US",context)
                },
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            start = completeText.indexOf(firstLinkText, end)
        }

        val secondLinkText = "prefix"
        val secondLinkStartIndex = completeText.indexOf(secondLinkText)
        val secondLinkEndIndex = secondLinkStartIndex + secondLinkText.length
        spannableString.setSpan(
            createClickableSpan(context,secondLinkText) {
                startActivity("https://en.m.wikipedia.org/wiki/List_of_country_calling_codes#Alphabetical_listing_by_country_or_region",context)
            },
            secondLinkStartIndex,
            secondLinkEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val thirdLinkText = "here."
        var thirdLinkStartIndex = completeText.indexOf(thirdLinkText)
        while (thirdLinkStartIndex != -1) {
            val thirdLinkEndIndex = thirdLinkStartIndex + thirdLinkText.length
            spannableString.setSpan(
                createClickableSpan(context,thirdLinkText) {
                    startActivity("https://faq.whatsapp.com/general/about-international-phone-number-format?lang=en-US",context)
                },
                thirdLinkStartIndex,
                thirdLinkEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            thirdLinkStartIndex = completeText.indexOf(thirdLinkText, thirdLinkEndIndex)
        }

        customDialogBinding.helpText.text = spannableString
        customDialogBinding.helpText.movementMethod = LinkMovementMethod.getInstance()
        customDialogBinding.helpText.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    fun createClickableSpanForSettingForSignal(context: Context,customSettingPopupLayoutBinding: CustomSettingPopupLayoutBinding) {
        val completeText = context.getString(R.string.experimental_signal_support)
        val spannableString = SpannableString(completeText)

        val firstLinkText = "Signal"
        var start = completeText.indexOf(firstLinkText)

        while (start != -1) {
            val end = start + firstLinkText.length
            val clickableSpan = createClickableSpan(context,firstLinkText) {
                startActivity("https://www.signal.org/",context)
            }

            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            start = completeText.indexOf(firstLinkText, end)
        }

        customSettingPopupLayoutBinding.tvExperimentalSignalSupport.text = spannableString
        customSettingPopupLayoutBinding.tvExperimentalSignalSupport.movementMethod =
            LinkMovementMethod.getInstance()
        customSettingPopupLayoutBinding.tvExperimentalSignalSupport.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
    }


    fun createClickableSpanForSettingForTelegram(context: Context,customSettingPopupLayoutBinding: CustomSettingPopupLayoutBinding) {
        val completeText = context.getString(R.string.experimental_telegram_support)
        val spannableString = SpannableString(completeText)

        val firstLinkText = "Telegram"
        var start = completeText.indexOf(firstLinkText)

        while (start != -1) {
            val end = start + firstLinkText.length
            val clickableSpan = createClickableSpan(context,firstLinkText) {
                startActivity("https://telegram.org/?notgs=1",context)
            }

            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            start = completeText.indexOf(firstLinkText, end)
        }

        customSettingPopupLayoutBinding.tvExperimentalTelegramSupport.text = spannableString
        customSettingPopupLayoutBinding.tvExperimentalTelegramSupport.movementMethod =
            LinkMovementMethod.getInstance()
        customSettingPopupLayoutBinding.tvExperimentalTelegramSupport.setTextColor(
            ContextCompat.getColor(context,
                R.color.white
            )
        )
    }


    fun createClickableSpan(context: Context, linkText: String, onClick: () -> Unit): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(context, R.color.main_green)
            }
        }
    }


    fun startActivity(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }


    private fun isDarkTheme(context: Context): Boolean {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}



