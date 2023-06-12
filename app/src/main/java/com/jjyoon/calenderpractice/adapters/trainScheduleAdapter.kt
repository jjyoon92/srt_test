package com.jjyoon.calenderpractice.adapters

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jjyoon.calenderpractice.R
import com.jjyoon.calenderpractice.services.TrainItem
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.log

class trainScheduleAdapter : RecyclerView.Adapter<trainScheduleAdapter.ViewHolder>() {
    private val items: MutableList<TrainItem> = mutableListOf()

    fun setData(data: List<TrainItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged() // 리스트의 크기와 아이템이 둘 다 변경되는 경우 ( 대체 가능? )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): trainScheduleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.train_schedule_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrainNo: TextView = itemView.findViewById(R.id.tvTrainScheduleTrainNo)
        private val tvDepartureStation: TextView =
            itemView.findViewById(R.id.tvTrainScheduleDepartureStation)
        private val tvDepartureTime: TextView =
            itemView.findViewById(R.id.tvTrainScheduleDepartureTime)
        private val tvArrivalStation: TextView =
            itemView.findViewById(R.id.tvTrainScheduleArrivalStation)
        private val tvArrivalTime: TextView = itemView.findViewById(R.id.tvTrainScheduleArrivalTime)

        val seatGradeSelectRadioGroup = itemView.findViewById<RadioGroup>(R.id.radioGroupSeatGradeSelect)
        val radioBtnSpecialSeatSelect = itemView.findViewById<RadioButton>(R.id.radioBtnSpecialSeatSelect)
        val radioBtnCommonSeatSelect = itemView.findViewById<RadioButton>(R.id.radioBtnCommonSeatSelect)
        val trainScheduleDropdownMenu = itemView.findViewById<LinearLayout>(R.id.trainScheduleDropdownMenuLl)

        val commonSeatText = SpannableString("일반실\n예약가능")
        val specialSeatText = SpannableString("특실\n예약가능")


        fun bind(trainItem: TrainItem) {

            commonSeatText.setSpan(ForegroundColorSpan(Color.BLACK), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            commonSeatText.setSpan(ForegroundColorSpan(Color.MAGENTA), 4, commonSeatText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            specialSeatText.setSpan(ForegroundColorSpan(Color.BLACK), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            specialSeatText.setSpan(ForegroundColorSpan(Color.MAGENTA), 3, specialSeatText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            radioBtnCommonSeatSelect.text = commonSeatText
            radioBtnSpecialSeatSelect.text = specialSeatText

            trainScheduleDropdownMenu.visibility = View.GONE

            radioBtnSpecialSeatSelect.setOnClickListener {
                radioBtnSpecialSeatSelect.setBackgroundColor(Color.CYAN)
                radioBtnCommonSeatSelect.setBackgroundColor(Color.WHITE)
                trainScheduleDropdownMenu.visibility = View.VISIBLE
            }

            radioBtnCommonSeatSelect.setOnClickListener {
                radioBtnCommonSeatSelect.setBackgroundColor(Color.CYAN)
                radioBtnSpecialSeatSelect.setBackgroundColor(Color.WHITE)
                trainScheduleDropdownMenu.visibility = View.VISIBLE
            }


            // 시간 형식 변경
            val originalFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val departureTime = originalFormat.parse(trainItem.depPlandTime)
            val arrivalTime = originalFormat.parse(trainItem.arrPlandTime)
            val formattedDepartureTime = targetFormat.format(departureTime)
            val formattedArrivalTime = targetFormat.format(arrivalTime)

            tvTrainNo.text = trainItem.trainNo.toString()
            tvDepartureStation.text = trainItem.depPlaceName
            tvDepartureTime.text = formattedDepartureTime
            tvArrivalStation.text = trainItem.arrPlaceName
            tvArrivalTime.text = formattedArrivalTime


        }

    }


}