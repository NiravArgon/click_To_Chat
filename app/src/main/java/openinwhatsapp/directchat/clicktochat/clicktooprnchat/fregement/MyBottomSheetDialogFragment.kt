package openinwhatsapp.directchat.clicktochat.clicktooprnchat.fregement

import AppPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import openinwhatsapp.directchat.clicktochat.clicktooprnchat.utils.Constant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import openinwhatsapp.directchat.clicktochat.R

class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var phoneNumber: String
    private lateinit var message: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val whatsAppIcon = view.findViewById<LinearLayout>(R.id.whatsAppLayout)
        val telegramIcon = view.findViewById<LinearLayout>(R.id.telegramLayout)
        val signalIcon = view.findViewById<LinearLayout>(R.id.signalLayout)

        val appPreferences = AppPreferences(requireContext())

        telegramIcon.visibility =
            if (appPreferences.getTelegramSupport()) View.VISIBLE else View.GONE

        signalIcon.visibility =
            if (appPreferences.getSignalSupport()) View.VISIBLE else View.GONE


        whatsAppIcon.setOnClickListener {
            if (message.isEmpty()) {
                context?.let { it1 -> Constant.openWhatsAppChat(phoneNumber, it1) }
                dismiss()
            } else {
                context?.let { it1 ->
                    Constant.openWhatsAppChatWithChat(phoneNumber, message, it1)
                    dismiss()
                }
            }
        }

        telegramIcon.setOnClickListener {
            if (message.isEmpty()) {
                context?.let { it1 -> Constant.openTelegramChatByPhoneNumber(phoneNumber, it1) }
                dismiss()
            } else {
                context?.let { it1 ->
                    Constant.openTelegramChatWithMessage(
                        phoneNumber, message,
                        it1
                    )
                }
                dismiss()
            }
        }

        signalIcon.setOnClickListener {
            if (message.isEmpty()) {
                context?.let { it1 -> Constant.openSignalChat(phoneNumber, it1) }
            } else {
                context?.let { it1 ->
                    Constant.openSignalChatWithMessage(
                        phoneNumber,
                        message,
                        it1
                    )
                }
                dismiss()
            }
        }
    }

    fun setBottomSheet(
        phoneNumber: String,
        message: String,
    ) {
        this.phoneNumber = phoneNumber
        this.message = message
    }
}
