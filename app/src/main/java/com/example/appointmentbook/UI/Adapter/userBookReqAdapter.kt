package com.example.appointmentbook.UI.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointmentbook.R
import com.example.appointmentbook.data.AlllBookReq.AllBookReqDataItem

class userBookReqAdapter : RecyclerView.Adapter<userBookReqAdapter.ReqViewHolder>() {

    var list: ArrayList<AllBookReqDataItem> = arrayListOf()
        set(value) = run {
            field = value
        }

    var btnContact: ((position: Int, data: AllBookReqDataItem) -> Unit)? = null
        set(value) = run { field = value }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReqViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.book_req_item, parent, false)
        return ReqViewHolder(view)
    }

    override fun onBindViewHolder(holder: userBookReqAdapter.ReqViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() = list.size

    inner class ReqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slotTime: TextView = itemView.findViewById(R.id.bookSlotTime)
        private val bookSlotNumber: TextView = itemView.findViewById(R.id.bookSlotNumber)
        private val bookedTeacherName: TextView = itemView.findViewById(R.id.bookedDoctorName)
        private val bookStatus: TextView = itemView.findViewById(R.id.bookStatus)
        private val slotCard: CardView = itemView.findViewById(R.id.slotCard)
        private val btnContact: Button = itemView.findViewById(R.id.btnContact)

        fun bind(data: AllBookReqDataItem, position: Int) {
            slotTime.text = "From ${data.slot.start_time}"
            bookSlotNumber.text = "Slot: ${data.slot.id}"
            bookedTeacherName.text = "Doctor Name: Dr Name"
            bookStatus.text = "Status: ${data.status}"

            if (data.status == "rejected") {
                slotCard.setCardBackgroundColor(Color.rgb(239, 83, 80))
                markButtonDisable(btnContact)
            } else if (data.status == "requested"){
                slotCard.setCardBackgroundColor(Color.rgb(100, 181, 246))
                markButtonDisable(btnContact)
            }else {
                slotCard.setCardBackgroundColor(Color.rgb(102, 187, 106))
            }

            btnContact.setOnClickListener {
                this@userBookReqAdapter.btnContact?.invoke(position, data)
            }
        }

        private fun markButtonDisable(button: Button) {
            button.isEnabled = false
            button.setTextColor(Color.WHITE)
            button.setBackgroundColor(Color.GRAY)
        }
    }

}