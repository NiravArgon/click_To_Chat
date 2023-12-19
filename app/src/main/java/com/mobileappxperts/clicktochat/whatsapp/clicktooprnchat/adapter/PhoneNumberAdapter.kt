package com.mobileappxperts.clicktochat.whatsapp.clicktooprnchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobileappxperts.clicktochat.whatsapp.R

class PhoneNumberAdapter(
    private val context: Context,
    private val isPinnedList: Boolean,
    private var phoneNumbers: List<String>,
    private val onPinClickListener: (String) -> Unit,
    private val onUnpinClickListener: (String) -> Unit,
    private val onDeleteClickListener: (String) -> Unit,
    private val onItemClickListener: (String) -> Unit, // Add click listener
) : RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val pinButton: ImageView = itemView.findViewById(R.id.pinButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.pinDelete)
        val conNumber: ConstraintLayout = itemView.findViewById(R.id.con_main_number_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_phone_number, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val phoneNumber = phoneNumbers[position]

        holder.phoneNumberTextView.text = phoneNumber

        // Click listener for pin/unpin button
        val clickListener: (String) -> Unit = if (isPinnedList) {
            onUnpinClickListener
        } else {
            onPinClickListener
        }

        // Click listener for delete button
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(phoneNumber)
        }

        // Click listener for pin/unpin button
        holder.pinButton.setOnClickListener {
            clickListener(phoneNumber)
        }

        // Click listener for the item
        holder.conNumber.setOnClickListener {
            onItemClickListener(phoneNumber)
        }
    }

    override fun getItemCount(): Int {
        return phoneNumbers.size
    }

    fun updateData(updatedPhoneNumbers: List<String>) {
        phoneNumbers = updatedPhoneNumbers
        notifyDataSetChanged()
    }
}

