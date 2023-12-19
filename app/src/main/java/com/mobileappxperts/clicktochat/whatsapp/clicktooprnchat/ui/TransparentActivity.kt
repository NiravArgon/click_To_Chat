package com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.ui

import AppPreferences
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.adapter.PhoneNumberAdapter
import com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.fregement.MyBottomSheetDialogFragment
import com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.utils.Constant
import com.example.notisave.ads.AdmobAdManager.loadBannerAd
import com.google.ads.consent.ConsentInformation
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.mobileappxperts.clicktochat.whatsapp.R
import com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.utils.Constant.showRateUsDialog
import com.mobileappxperts.clicktochat.whatsapp.databinding.ActivityTransparentBinding
import com.mobileappxperts.clicktochat.whatsapp.databinding.CustomSettingPopupLayoutBinding
import com.mobileappxperts.clicktochat.whatsapp.databinding.DeleteDilogPopupLayoutBinding
import com.mobileappxperts.clicktochat.whatsapp.databinding.MainLayoutBinding
import com.mobileappxperts.clicktochat.whatsapp.databinding.SavedContectListPopupLayoutBinding
import com.mobileappxperts.clicktochat.whatsapp.databinding.TelegramAttentionDilogBinding
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import java.util.UUID

class TransparentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransparentBinding
    private lateinit var customDialogBinding: MainLayoutBinding
    private lateinit var customSettingPopupLayoutBinding: CustomSettingPopupLayoutBinding

    private var customDialog: Dialog? = null
    private var customSettingDialog: Dialog? = null
    private var customSaveContactDialog: Dialog? = null

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransparentBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Log.i("TAG", "onCreatesdfgsdfg: 0")

        ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown()

        Log.i("TAG", "onCreatesdfgsdfg: 1")

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }

        checkForUpdates()

        Log.i("TAG", "onCreatesdfgsdfg: 2")

        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        OneSignal.initWithContext(this, getString(R.string.one_signal_id))
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }

        customSettingPopupLayoutBinding = CustomSettingPopupLayoutBinding.inflate(layoutInflater)
        customSettingDialog = Dialog(this)
        customSettingDialog?.setContentView(customSettingPopupLayoutBinding.root)

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        customSettingDialog?.window?.setLayout(width, height)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            loadBannerAd(customSettingPopupLayoutBinding.bannerAds)
        }, 1500L)
        Log.i("TAG", "onCreatesdfgsdfg: 3")

        showCustomDialog()

        Log.i("TAG", "onCreatesdfgsdfg: 4")
    }


    private fun showCustomDialog() {

        val appPreferences = AppPreferences(this)

        if (!isFinishing) {
            customDialog = Dialog(this)
            customDialogBinding = MainLayoutBinding.inflate(layoutInflater)
            customDialog?.setContentView(customDialogBinding.root)
            customDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.9).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT

            customDialog?.window?.setLayout(width, height)


            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                loadBannerAd(customDialogBinding.bannerAds)
            }, 1500L)

            Constant.createClickableSpan(this, customDialogBinding)

            if (customDialogBinding.editTextCarrierNumber.text.isNullOrEmpty()) {
                Constant.disableButtons(customDialogBinding, this)
            } else {
                Constant.enableButtons(customDialogBinding, this)
            }

            if (appPreferences.getRestorePrefix()) {
                val countryCodeString = appPreferences.getCountryCode()
                if (countryCodeString.isNotEmpty()) {
                    val countryCodeInt = countryCodeString.toInt()
                    customDialogBinding.countryCodePicker.setCountryForPhoneCode(countryCodeInt)
                    Log.i("TAG", "Setting country code: $countryCodeInt")
                }
            } else {
                if (appPreferences.getCountryCode().isNotEmpty()) {
                    val countryCodeInt = appPreferences.getCountryCode().toInt()
                    customDialogBinding.countryCodePicker.setCountryForPhoneCode(countryCodeInt)
                    Log.i("TAG", "Setting country code: $countryCodeInt")
                }
            }


            customDialogBinding.countryCodePicker.setOnCountryChangeListener {
                if (!appPreferences.getRestorePrefix()) {
                    val countryCode =
                        customDialogBinding.countryCodePicker.selectedCountryCodeWithPlus
                    appPreferences.setCountryCode(countryCode)
                }
            }



            if (appPreferences.getHideHelpTextFromMain()) {
                customDialogBinding.helpText.visibility = View.GONE
                customDialogBinding.txHelpText.visibility = View.GONE
                customDialogBinding.sepInfo.visibility = View.GONE
            }

            customDialogBinding.ivSetting.setOnClickListener {
                openSettingDialog()
            }

            customDialogBinding.btDownArrow.setOnClickListener {
                if (customDialogBinding.lnrExtraButtons.visibility == View.VISIBLE) {
                    customDialogBinding.lnrExtraButtons.visibility = View.GONE
                    customDialogBinding.layoutExpanded.visibility = View.GONE
                    customDialogBinding.btDownArrow.setImageResource(R.drawable.ic_drop_down_white)
                } else {
                    customDialogBinding.lnrExtraButtons.visibility = View.VISIBLE
                    customDialogBinding.lnrExtraButtons.visibility = View.VISIBLE
                    customDialogBinding.layoutExpanded.visibility = View.VISIBLE
                    customDialogBinding.btDownArrow.setImageResource(R.drawable.ic_arrow_drop_white)
                }
            }

            customDialog?.setOnDismissListener {
                finish()
            }

            customDialogBinding.clearText.setOnClickListener {
                customDialogBinding.editTextCarrierNumber.text = null
            }

            customDialogBinding.editTextCarrierNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (appPreferences.getRemoveLeadingZeros()) {
                        val input = s.toString()
                        val result = input.removeLeadingZeros()
                        if (input != result) {
                            customDialogBinding.editTextCarrierNumber.setText(result)
                            customDialogBinding.editTextCarrierNumber.setSelection(result.length)
                        }
                    }


                    if (s.isNullOrEmpty()) {
                        Constant.disableButtons(customDialogBinding, this@TransparentActivity)
                    } else {
                        Constant.enableButtons(customDialogBinding, this@TransparentActivity)
                    }
                }
            })


            customDialogBinding.btnOpen.setOnClickListener {
                if (!customDialogBinding.editTextCarrierNumber.text.isNullOrEmpty()) {
                    val phoneNumber = Constant.getFormattedPhoneNumber(customDialogBinding)
                    val message = customDialogBinding.etMessage.text.toString()

                    if (appPreferences.getSaveRecentAndMessages()) {
                        appPreferences.addPhoneNumber(phoneNumber)
                    }

                    if (!appPreferences.getSignalSupport() && !appPreferences.getTelegramSupport()) {
                        if (customDialogBinding.etMessage.text.isNullOrEmpty()) {
                            Constant.openWhatsAppChat(phoneNumber, this@TransparentActivity)
                        } else {
                            Constant.openWhatsAppChatWithChat(
                                phoneNumber,
                                message,
                                this@TransparentActivity
                            )
                        }
                    } else {
                        val bottomSheetFragment = MyBottomSheetDialogFragment()
                        bottomSheetFragment.setBottomSheet(phoneNumber, message)
                        bottomSheetFragment.show(
                            supportFragmentManager,
                            bottomSheetFragment.tag
                        )
                    }


                }
            }

            customDialogBinding.btnCall.setOnClickListener {
                if (!customDialogBinding.editTextCarrierNumber.text.isNullOrEmpty()) {
                    val phoneNumber = Constant.getFormattedPhoneNumber(customDialogBinding)
                    Constant.makePhoneCall(phoneNumber, this)
                }
            }

            customDialogBinding.btnShare.setOnClickListener {
                if (!customDialogBinding.editTextCarrierNumber.text.isNullOrEmpty()) {
                    val phoneNumber = Constant.getFormattedPhoneNumber(customDialogBinding)
                    Constant.shareLink(phoneNumber, this)
                }
            }

            customDialogBinding.btnSms.setOnClickListener {
                if (!customDialogBinding.editTextCarrierNumber.text.isNullOrEmpty()) {
                    val phoneNumber = Constant.getFormattedPhoneNumber(customDialogBinding)
                    val message = customDialogBinding.etMessage.text.toString()
                    Constant.openSmsWithMessage(phoneNumber, message, this)
                }
            }

            customDialogBinding.btnShortcut.setOnClickListener {
                if (!customDialogBinding.editTextCarrierNumber.text.isNullOrEmpty()) {
                    val phoneNumber = Constant.getFormattedPhoneNumber(customDialogBinding)

                    val message = customDialogBinding.etMessage.text.toString()

                    val randomId = UUID.randomUUID().toString()


                    if (customDialogBinding.etMessage.text.isNullOrEmpty()) {
                        Constant.createWhatsAppChatShortcut(phoneNumber, randomId, this)
                    } else {
                        Constant.createWhatsAppChatWithMessageShortcut(
                            phoneNumber,
                            message,
                            randomId,
                            this
                        )
                    }
                }
            }

            customDialogBinding.ivList.setOnClickListener {
                if (appPreferences.getPhoneNumbers().toList()
                        .isNotEmpty() || appPreferences.getPinnedPhoneNumbers().toList()
                        .isNotEmpty()
                ) {
                    openSaveNumberDialog()
                } else {
                    Toast.makeText(
                        this@TransparentActivity,
                        "there is not data to show",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            customDialogBinding.ivPast.setOnClickListener {
                val lastCopiedText = Constant.getLastCopiedText(this)
                customDialogBinding.editTextCarrierNumber.setText(lastCopiedText)
            }

            /*     customDialog?.window?.decorView?.setOnTouchListener { _, event ->
                     if (event.action == MotionEvent.ACTION_OUTSIDE) {
                         return@setOnTouchListener true
                     }
                     false
                 }
     */


            customDialog?.show()
        }

    }

    private fun openSettingDialog() {
        val appPreferences = AppPreferences(this)

        Constant.createClickableSpanForSettingForSignal(this, customSettingPopupLayoutBinding)
        Constant.createClickableSpanForSettingForTelegram(this, customSettingPopupLayoutBinding)

        customSettingPopupLayoutBinding.scSaveRecentAndMessages.isChecked =
            appPreferences.getSaveRecentAndMessages()
        customSettingPopupLayoutBinding.scMergePrefix.isChecked = appPreferences.getMergePrefix()
        customSettingPopupLayoutBinding.scRemoveLeadingZeros.isChecked =
            appPreferences.getRemoveLeadingZeros()
        customSettingPopupLayoutBinding.scHideTheHelpTextFromMain.isChecked =
            appPreferences.getHideHelpTextFromMain()
        customSettingPopupLayoutBinding.scRestorePrefix.isChecked =
            appPreferences.getRestorePrefix()
        customSettingPopupLayoutBinding.scOpenAutomaticallyWhenAppClose.isChecked =
            appPreferences.getOpenAutomaticallyWhenAppClose()
        customSettingPopupLayoutBinding.scSignal.isChecked = appPreferences.getSignalSupport()
        customSettingPopupLayoutBinding.scTelegram.isChecked = appPreferences.getTelegramSupport()


        if (appPreferences.getRestorePrefix()) {
            val originalString = getString(R.string.restore_prefix_none_when_the_app_closes)
            val yourNumber =
                "[" + customDialogBinding.countryCodePicker.selectedCountryCodeWithPlus + "]"
            val modifiedString = String.format(originalString, yourNumber)
            customSettingPopupLayoutBinding.restorePrefixNoneWhenTheAppCloses.text = modifiedString
        } else {
            val originalString = getString(R.string.restore_prefix_none_when_the_app_closes)
            val yourNumber = "[" + appPreferences.getCountryCode() + "]"
            val modifiedString = String.format(originalString, yourNumber)
            customSettingPopupLayoutBinding.restorePrefixNoneWhenTheAppCloses.text = modifiedString
        }


        customSettingPopupLayoutBinding.scSaveRecentAndMessages.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setSaveRecentAndMessages(isChecked)
        }

        customSettingPopupLayoutBinding.scMergePrefix.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setMergePrefix(isChecked)
        }

        customSettingPopupLayoutBinding.scRemoveLeadingZeros.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setRemoveLeadingZeros(isChecked)
        }

        customSettingPopupLayoutBinding.scHideTheHelpTextFromMain.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setHideHelpTextFromMain(isChecked)

            if (appPreferences.getHideHelpTextFromMain()) {
                customDialogBinding.helpText.visibility = View.GONE
                customDialogBinding.txHelpText.visibility = View.GONE
                customDialogBinding.sepInfo.visibility = View.GONE
            } else {
                customDialogBinding.helpText.visibility = View.VISIBLE
                customDialogBinding.txHelpText.visibility = View.VISIBLE
                customDialogBinding.sepInfo.visibility = View.VISIBLE
            }
        }

        customSettingPopupLayoutBinding.scRestorePrefix.setOnCheckedChangeListener { _, isChecked ->

            appPreferences.setRestorePrefix(isChecked)
            if (isChecked) {
                appPreferences.setCountryCode(customDialogBinding.countryCodePicker.selectedCountryCodeWithPlus)
                val originalString = getString(R.string.restore_prefix_none_when_the_app_closes)
                val countryCode = customDialogBinding.countryCodePicker.selectedCountryCodeWithPlus
                val yourNumber = "[$countryCode]"
                val modifiedString = String.format(originalString, yourNumber)
                customSettingPopupLayoutBinding.restorePrefixNoneWhenTheAppCloses.text =
                    modifiedString
                customDialogBinding.countryCodePicker.setCountryForPhoneCode(countryCode.toInt())
            }
        }

        customSettingPopupLayoutBinding.scOpenAutomaticallyWhenAppClose.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setOpenAutomaticallyWhenAppClose(isChecked)
        }

        customSettingPopupLayoutBinding.scTelegram.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                openAttentionDialog()
            } else {
                customSettingPopupLayoutBinding.scTelegram.isChecked = false
            }
        }
        customSettingPopupLayoutBinding.scSignal.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setSignalSupport(isChecked)
        }

        customSettingPopupLayoutBinding.tvDeleteAppData.setOnClickListener {
            openDeleteDialog()
        }

        customSettingDialog?.show()
    }


    private lateinit var allContactsAdapter: PhoneNumberAdapter
    private lateinit var pinnedContactsAdapter: PhoneNumberAdapter

    private fun openSaveNumberDialog() {
        val appPreferences = AppPreferences(this)

        val savedContectListPopupLayoutBinding =
            SavedContectListPopupLayoutBinding.inflate(layoutInflater)
        customSaveContactDialog = Dialog(this)
        customSaveContactDialog?.setContentView(savedContectListPopupLayoutBinding.root)

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT

        customSaveContactDialog?.window?.setLayout(width, height)

        val allPhoneNumbers = appPreferences.getPhoneNumbers().toList()
        val pinnedPhoneNumbers = appPreferences.getPinnedPhoneNumbers().toList()

        allContactsAdapter = PhoneNumberAdapter(this, false, allPhoneNumbers,
            onPinClickListener = { phoneNumber ->
                appPreferences.addPinnedPhoneNumber(phoneNumber)
                appPreferences.removePhoneNumber(phoneNumber)
                updateAdapterData()
            },
            onUnpinClickListener = { phoneNumber ->
                appPreferences.removePinnedPhoneNumber(phoneNumber)
                updateAdapterData()
            },
            onDeleteClickListener = { phoneNumber ->
                appPreferences.removePhoneNumber(phoneNumber)
                updateAdapterData()
            },
            onItemClickListener = { phoneNumber ->
                openDialogForItem(phoneNumber)
            })

        pinnedContactsAdapter = PhoneNumberAdapter(this, true, pinnedPhoneNumbers,
            onPinClickListener = { phoneNumber ->
                appPreferences.addPinnedPhoneNumber(phoneNumber)
                updateAdapterData()
            },
            onUnpinClickListener = { phoneNumber ->
                appPreferences.removePinnedPhoneNumber(phoneNumber)
                appPreferences.addPhoneNumber(phoneNumber)
                updateAdapterData()
            },
            onDeleteClickListener = { phoneNumber ->
                appPreferences.removePinnedPhoneNumber(phoneNumber)
                updateAdapterData()
            },
            onItemClickListener = { phoneNumber ->
                openDialogForItem(phoneNumber)
            })

        savedContectListPopupLayoutBinding.recyclerViewAllContacts.layoutManager =
            GridLayoutManager(this, 1)
        savedContectListPopupLayoutBinding.recyclerViewAllContacts.adapter = allContactsAdapter

        savedContectListPopupLayoutBinding.recyclerViewPinnedContacts.layoutManager =
            GridLayoutManager(this, 1)
        savedContectListPopupLayoutBinding.recyclerViewPinnedContacts.adapter =
            pinnedContactsAdapter

        savedContectListPopupLayoutBinding.closeButton.setOnClickListener {
            customSaveContactDialog?.dismiss()
        }

        customSaveContactDialog?.show()
    }

    private fun updateAdapterData() {
        val appPreferences = AppPreferences(this)
        allContactsAdapter.updateData(appPreferences.getPhoneNumbers().toList())
        pinnedContactsAdapter.updateData(appPreferences.getPinnedPhoneNumbers().toList())
    }

    private fun openDialogForItem(phoneNumber: String) {

        val phoneNumberWithCountryCode = phoneNumber

        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        try {
            val phoneNumber = phoneNumberUtil.parse(phoneNumberWithCountryCode, null)
            val countryCode = phoneNumber.countryCode
            val nationalNumber = phoneNumber.nationalNumber

            customDialogBinding.countryCodePicker.setCountryForPhoneCode(countryCode)
            if (!nationalNumber.equals(null)) {
                customDialogBinding.editTextCarrierNumber.setText(nationalNumber.toString())
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        customSaveContactDialog?.dismiss()
    }


    private fun openDeleteDialog() {
        lateinit var deleteDialogPopupLayoutBinding: DeleteDilogPopupLayoutBinding
        var deleteDialog: Dialog? = null

        deleteDialogPopupLayoutBinding = DeleteDilogPopupLayoutBinding.inflate(layoutInflater)
        deleteDialog = Dialog(this)
        deleteDialog?.setContentView(deleteDialogPopupLayoutBinding.root)


        val appPreferences = AppPreferences(this)
        deleteDialogPopupLayoutBinding.tvOk.setOnClickListener {

            val packageManager = this.packageManager
            val applicationInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val cacheDir = File(applicationInfo.dataDir, "cache")
            cacheDir.deleteRecursively()

            Constant.clearAppData(this)

            appPreferences.setSaveRecentAndMessages(true)
            appPreferences.setRemoveLeadingZeros(true)
            appPreferences.setRestorePrefix(true)
            finish()

        }

        deleteDialogPopupLayoutBinding.tvCancel.setOnClickListener {
            deleteDialog?.dismiss()
        }


        deleteDialog?.show()
    }


    private fun openAttentionDialog() {
        lateinit var telegramAttentionDilogBinding: TelegramAttentionDilogBinding
        var customAttentionDialog: Dialog? = null

        telegramAttentionDilogBinding = TelegramAttentionDilogBinding.inflate(layoutInflater)
        customAttentionDialog = Dialog(this)
        customAttentionDialog?.setContentView(telegramAttentionDilogBinding.root)

        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT

        customAttentionDialog?.window?.setLayout(width, height)

        val appPreferences = AppPreferences(this)
        telegramAttentionDilogBinding.tvOk.setOnClickListener {
            appPreferences.setTelegramSupport(true)
            customSettingPopupLayoutBinding.scTelegram.isChecked = true
            customAttentionDialog?.dismiss()
        }


        telegramAttentionDilogBinding.tvCancel.setOnClickListener {
            customAttentionDialog?.dismiss()
            appPreferences.setTelegramSupport(false)
            customSettingPopupLayoutBinding.scTelegram.isChecked = false
        }


        customAttentionDialog?.show()
    }


    override fun onResume() {
        super.onResume()
        showRateUsDialog()
    }

    override fun onDestroy() {
        val appPreferences = AppPreferences(this)
        val countryCode = customDialogBinding.countryCodePicker?.selectedCountryCodeWithPlus
        if (appPreferences.getRestorePrefix().not()) {
            appPreferences.setCountryCode(countryCode)
        }
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
        super.onDestroy()
    }

    fun String.removeLeadingZeros(removeZeros: Boolean = true): String {
        return if (removeZeros) {
            this.replaceFirst("^0+".toRegex(), "")
        } else {
            this
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds.",
                Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch {
                delay(5000)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun checkForUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isAppAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isAppAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    123
                )
            }
        }.addOnFailureListener {
            Log.e("TAG111", "checkForUpdates: " + it.message)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            when (resultCode) {
                RESULT_CANCELED -> {
                    finish()
                }

                RESULT_OK -> {
//                    startFlow()
                    Toast.makeText(this, "app is UpDated", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
