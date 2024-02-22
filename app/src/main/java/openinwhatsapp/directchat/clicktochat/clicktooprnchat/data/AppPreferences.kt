import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar.getInstance

class AppPreferences(context: Context) {

    private val PREFS_NAME = "YourPrefs"
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Define your keys for each setting
    private val KEY_SAVE_RECENT_AND_MESSAGES = "saveRecentAndMessages"
    private val KEY_MERGE_PREFIX = "mergePrefix"
    private val KEY_REMOVE_LEADING_ZEROS = "removeLeadingZeros"
    private val KEY_HIDE_HELP_TEXT_FROM_MAIN = "hideHelpTextFromMain"
    private val KEY_RESTORE_PREFIX = "restorePrefix"
    private val KEY_OPEN_AUTOMATICALLY_WHEN_APP_CLOSE = "openAutomaticallyWhenAppClose"
    private val KEY_PINNED_PHONE_NUMBERS = "pinnedPhoneNumbers"
    private val KEY_SIGNNAL_SUPPORT = "signalSupport"
    private val KEY_TELEGRAM_SUPPORT = "telegramSupport"


    fun getSaveRecentAndMessages(): Boolean {
        return prefs.getBoolean(KEY_SAVE_RECENT_AND_MESSAGES, true)
    }

    fun setSaveRecentAndMessages(value: Boolean) {
        prefs.edit().putBoolean(KEY_SAVE_RECENT_AND_MESSAGES, value).apply()
    }

    fun getMergePrefix(): Boolean {
        return prefs.getBoolean(KEY_MERGE_PREFIX, false)
    }

    fun setMergePrefix(value: Boolean) {
        prefs.edit().putBoolean(KEY_MERGE_PREFIX, value).apply()
    }

    fun getRemoveLeadingZeros(): Boolean {
        return prefs.getBoolean(KEY_REMOVE_LEADING_ZEROS, true)
    }

    fun setRemoveLeadingZeros(value: Boolean) {
        prefs.edit().putBoolean(KEY_REMOVE_LEADING_ZEROS, value).apply()
    }

    fun getHideHelpTextFromMain(): Boolean {
        return prefs.getBoolean(KEY_HIDE_HELP_TEXT_FROM_MAIN, false)
    }

    fun setHideHelpTextFromMain(value: Boolean) {
        prefs.edit().putBoolean(KEY_HIDE_HELP_TEXT_FROM_MAIN, value).apply()
    }

    fun getRestorePrefix(): Boolean {
        return prefs.getBoolean(KEY_RESTORE_PREFIX, false)
    }

    fun setRestorePrefix(value: Boolean) {
        prefs.edit().putBoolean(KEY_RESTORE_PREFIX, value).apply()
    }

    fun getOpenAutomaticallyWhenAppClose(): Boolean {
        return prefs.getBoolean(KEY_OPEN_AUTOMATICALLY_WHEN_APP_CLOSE, false)
    }

    fun setOpenAutomaticallyWhenAppClose(value: Boolean) {
        prefs.edit().putBoolean(KEY_OPEN_AUTOMATICALLY_WHEN_APP_CLOSE, value).apply()
    }

    fun getSignalSupport(): Boolean {
        return prefs.getBoolean(KEY_SIGNNAL_SUPPORT, false)
    }

    fun setSignalSupport(value: Boolean) {
        prefs.edit().putBoolean(KEY_SIGNNAL_SUPPORT, value).apply()
    }

    fun getTelegramSupport(): Boolean {
        return prefs.getBoolean(KEY_TELEGRAM_SUPPORT, false)
    }

    fun setTelegramSupport(value: Boolean) {
        prefs.edit().putBoolean(KEY_TELEGRAM_SUPPORT, value).apply()
    }


    fun setCountryCode(countryCode: String?) {
        prefs.edit().putString("countryCode", countryCode).apply()
    }

    fun getCountryCode(): String {
        return prefs.getString("countryCode", "") ?: ""
    }

    fun setShouldSetCountryCode(shouldSetCountryCode: Boolean) {
        prefs.edit().putBoolean("shouldSetCountryCode", shouldSetCountryCode).apply()
    }

    fun shouldSetCountryCode(): Boolean {
        return prefs.getBoolean("shouldSetCountryCode", false)
    }


    // Method to add a new number to the list
    fun addPhoneNumber(phoneNumber: String) {
        val currentNumbers = getPhoneNumbers().toMutableSet()
        currentNumbers.add(phoneNumber)
        prefs.edit().putStringSet("phoneNumbers", currentNumbers).apply()
    }

    // Method to get the list of phone numbers
    fun getPhoneNumbers(): Set<String> {
        return prefs.getStringSet("phoneNumbers", emptySet()) ?: emptySet()
    }

    // Method to remove a number from the list
    fun removePhoneNumber(phoneNumber: String) {
        val currentNumbers = getPhoneNumbers().toMutableSet()
        currentNumbers.remove(phoneNumber)
        prefs.edit().putStringSet("phoneNumbers", currentNumbers).apply()
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
    }

    // Method to add a new number to the list of pinned phone numbers
    fun addPinnedPhoneNumber(phoneNumber: String) {
        val currentPinnedNumbers = getPinnedPhoneNumbers().toMutableSet()
        currentPinnedNumbers.add(phoneNumber)
        prefs.edit().putStringSet(KEY_PINNED_PHONE_NUMBERS, currentPinnedNumbers).apply()
    }

    // Method to get the list of pinned phone numbers
    fun getPinnedPhoneNumbers(): Set<String> {
        return prefs.getStringSet(KEY_PINNED_PHONE_NUMBERS, emptySet()) ?: emptySet()
    }

    // Method to remove a number from the list of pinned phone numbers
    fun removePinnedPhoneNumber(phoneNumber: String) {
        val currentPinnedNumbers = getPinnedPhoneNumbers().toMutableSet()
        currentPinnedNumbers.remove(phoneNumber)
        prefs.edit().putStringSet(KEY_PINNED_PHONE_NUMBERS, currentPinnedNumbers).apply()
    }

    fun startTime(long: Long) {
        prefs.edit().putLong("startTime", long).apply()
    }

    fun getstartTime(): Long {
        return prefs.getLong("startTime", 0) ?: 0
    }


    fun checkDaysForReviewDialog(long: Long) {
        prefs.edit().putLong("lastShownDate", long).apply()
    }

    fun getcheckDaysForReviewDialog(): Long {
        return prefs.getLong("lastShownDate", System.currentTimeMillis()) ?: 0
    }

}
