package com.jjyoon.calenderpractice

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var btnDepartureOpenCalendar: Button
    private lateinit var btnDepartureOpenCalendarText: String
    private lateinit var btnArrivalOpenCalendar: Button
    private lateinit var btnArrivalOpenCalendarText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDepartureOpenCalendar = findViewById(R.id.btnOpenDepartureCalendar)
        btnArrivalOpenCalendar = findViewById(R.id.btnOpenArrivalCalendar)

        val currentDate = getCurrentDate()
        btnDepartureOpenCalendarText = currentDate
        btnArrivalOpenCalendarText = currentDate

        btnDepartureOpenCalendar.setOnClickListener {
            val datePickerIntent = Intent(this, DepartureCalendarActivity::class.java)
            DepartureCalendarActivityResult.launch(datePickerIntent)
        }

        btnArrivalOpenCalendar.setOnClickListener {
            val datePickerIntent = Intent(this, ArrivalCalendarActivity::class.java)
            ArrivalCalendarActivityResult.launch(datePickerIntent)
        }

//        println("출발날짜 : " + btnDatePickerText)

        btnDepartureOpenCalendar.setText("출발일 : $btnDepartureOpenCalendarText")
        btnArrivalOpenCalendar.setText("도착일 : $btnArrivalOpenCalendarText")
    }

    // BtnDatePicker 날짜, 요일, 시간 초기값 반환
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val date = dateFormat.format(calendar.time)
        val timeFormat = SimpleDateFormat("HH시", Locale.getDefault())
        val time = timeFormat.format(calendar.time)
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }

        return "${date}(${dayOfWeek}) ${time} 이후"
    }


    // DatePicker
    private val DepartureCalendarActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedDate = data?.getStringExtra("selectedDate")
                if (selectedDate != null) {
                    btnDepartureOpenCalendarText = selectedDate
                    val selectedTime = data?.getIntExtra("selectedTime", 0)
                    if (selectedTime != null) {
                        if (selectedTime < 10) {
                            btnDepartureOpenCalendar.text = "출발일 : $selectedDate 0${selectedTime}시 이후"
                        } else {
                            btnDepartureOpenCalendar.text = "출발일 : $selectedDate ${selectedTime}시 이후"
                        }
                    }
                }
            }
        }

    private val ArrivalCalendarActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedDate = data?.getStringExtra("selectedDate")
                if (selectedDate != null) {
                    btnArrivalOpenCalendarText = selectedDate
                    val selectedTime = data?.getIntExtra("selectedTime", 0)
                    if (selectedTime != null) {
                        if (selectedTime < 10) {
                            btnArrivalOpenCalendar.text = "도착일 : $selectedDate 0${selectedTime}시 이후"
                        } else {
                            btnArrivalOpenCalendar.text = "도착일 : $selectedDate ${selectedTime}시 이후"
                        }
                    }
                }
            }
        }


}