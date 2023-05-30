package com.jjyoon.calenderpractice

import android.app.Activity
import android.content.Context
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
                val selectedDateMillis = data?.getLongExtra("selectedDateMillis", 0)

                // SharedPreferences 로 이전에 선택한 날짜 저장
//                val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
//                val editor = sharedPref.edit()
//                editor.putLong("DepartureSelectedDate", selectedDateMillis ?: 0)
//                editor.apply()

                val selectedDate = data?.getStringExtra("selectedDate")
                val selectedYear = data?.getStringExtra("selectedYear")
                val selectedMonth = data?.getStringExtra("selectedMonth")
                val selectedDay = data?.getStringExtra("selectedDay")
                val selectedDayOfWeek = data?.getStringExtra("selectedDayOfWeek")
                if (data != null) {
                    println("data not null")
//                    btnDepartureOpenCalendarText = selectedDate
                    val selectedTime = data?.getIntExtra("selectedTime", 0)
                    if (selectedTime != null) {
                        if (selectedTime < 10) {
                            btnDepartureOpenCalendarText = "출발일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) 0${selectedTime}시 이후"
                            btnDepartureOpenCalendar.text = btnDepartureOpenCalendarText
//                            btnArrivalOpenCalendar.text = "도착일 : $selectedDate 0${selectedTime}시 이후"
                        } else {
                            btnDepartureOpenCalendarText = "출발일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) ${selectedTime}시 이후"
                            btnDepartureOpenCalendar.text = btnDepartureOpenCalendarText
//                            btnArrivalOpenCalendar.text = "도착일 : $selectedDate ${selectedTime}시 이후"
                        }
                    }
                }
            }
        }

    private val ArrivalCalendarActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedDateMillis = data?.getLongExtra("selectedDateMillis", 0)

                // SharedPreferences 로 이전에 선택한 날짜 저장
                val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putLong("DepartureSelectedDate", selectedDateMillis ?: 0)
                editor.apply()

                val selectedDate = data?.getStringExtra("selectedDate")
                val selectedYear = data?.getStringExtra("selectedYear")
                val selectedMonth = data?.getStringExtra("selectedMonth")
                val selectedDay = data?.getStringExtra("selectedDay")
                val selectedDayOfWeek = data?.getStringExtra("selectedDayOfWeek")
                println(selectedYear + selectedMonth + selectedDay + selectedDayOfWeek)
                if (data != null) {
//                    println("data not null")
//                    btnArrivalOpenCalendarText = selectedDate
                    val selectedTime = data?.getLongExtra("selectedTime", 0)
                    if (selectedTime != null) {
                        if (selectedTime < 10) {
                            btnArrivalOpenCalendarText = "도착일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) 0${selectedTime}시 이후"
                            btnArrivalOpenCalendar.text = btnArrivalOpenCalendarText
//                            btnArrivalOpenCalendar.text = "도착일 : $selectedDate 0${selectedTime}시 이후"
                        } else {
                            btnArrivalOpenCalendarText = "도착일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) ${selectedTime}시 이후"
                            btnArrivalOpenCalendar.text = btnArrivalOpenCalendarText
//                            btnArrivalOpenCalendar.text = "도착일 : $selectedDate ${selectedTime}시 이후"
                        }
                    }
                }
            }
        }


}