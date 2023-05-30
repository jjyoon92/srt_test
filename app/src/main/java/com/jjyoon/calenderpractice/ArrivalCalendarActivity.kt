package com.jjyoon.calenderpractice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jjyoon.calenderpractice.adapters.TimeSelectAdapter
import java.util.*

class ArrivalCalendarActivity : AppCompatActivity(), TimeSelectAdapter.OnTimeClickListener {

    private lateinit var timeSelectAdapter: RecyclerView.Adapter<*>
    private lateinit var selectedDate: String
    private lateinit var selectedYear: String
    private lateinit var selectedMonth: String
    private lateinit var selectedDay: String
    private lateinit var selectedDayOfWeek: String
    private lateinit var dayOfWeek: String
    private var selectedTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arrival_calendar)

        val calendarView: CalendarView = findViewById(R.id.arrivalCalendarView)
        val btnGet: Button = findViewById(R.id.btnArrivalDateSelect)
        val calendar = Calendar.getInstance()
        val arrivalCalendar = Calendar.getInstance()

        // DatePicker 최소 날짜를 오늘 날짜로 설정
        calendarView.minDate = calendar.timeInMillis

        // 24시간
        val times = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23) // 시간 리스트
        timeSelectAdapter =
            TimeSelectAdapter(this, times, this) // this : Calendar Activity 클래스 인스턴스, this:
        val timeSelectRecyclerView: RecyclerView = findViewById(R.id.arrivalTimeSelectRecyclerView)
        timeSelectRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        timeSelectRecyclerView.adapter = timeSelectAdapter

        // calendarView dateChangeListener 설정
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            arrivalCalendar.set(year, month, dayOfMonth)
        }

        // 날짜와 시간 값 확정
        btnGet.setOnClickListener {
            val selectedMillis = calendarView.date
//            arrivalCalendar.timeInMillis = selectedMillis

            val dayOfWeekNumber: Int = arrivalCalendar.get(Calendar.DAY_OF_WEEK)

            when (dayOfWeekNumber) {
                1 -> dayOfWeek = "일"
                2 -> dayOfWeek = "월"
                3 -> dayOfWeek = "화"
                4 -> dayOfWeek = "수"
                5 -> dayOfWeek = "목"
                6 -> dayOfWeek = "금"
                7 -> dayOfWeek = "토"
            }

//            val selectedDateString =
//                "${calenderView.year}년 ${calenderView.month + 1}월 ${calenderView.dayOfMonth}일(${dayOfWeek})"


            selectedYear = arrivalCalendar.get(Calendar.YEAR).toString()
            selectedMonth = (arrivalCalendar.get(Calendar.MONTH) + 1).toString()
            selectedDay = (arrivalCalendar.get(Calendar.DAY_OF_MONTH)).toString()
            selectedDayOfWeek = dayOfWeek

            val selectedDateString =
                "${arrivalCalendar.get(Calendar.YEAR)}년 ${arrivalCalendar.get(Calendar.MONTH) + 1}월 ${arrivalCalendar.get(Calendar.DAY_OF_MONTH)}일($dayOfWeek)"


            selectedDate = selectedDateString

            if (::timeSelectAdapter.isInitialized) {
                sendResult()
            }
        }
    }

    // TimeNumberClick
    override fun onTimeClick(time: Int) {
        selectedTime = time
    }

    private fun sendResult() {
        val resultIntent = Intent()
//        resultIntent.putExtra("selectedDate", selectedDate)
        resultIntent.putExtra("selectedYear", selectedYear)
        resultIntent.putExtra("selectedMonth", selectedMonth)
        resultIntent.putExtra("selectedDay", selectedDay)
        resultIntent.putExtra("selectedDayOfWeek", selectedDayOfWeek)
        resultIntent.putExtra("selectedTime", selectedTime)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}