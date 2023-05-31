package com.jjyoon.calenderpractice

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var btnDepartureOpenCalendar: Button
    private lateinit var btnDepartureOpenCalendarText: String
    private lateinit var btnArrivalOpenCalendar: Button
    private lateinit var btnArrivalOpenCalendarText: String
    private lateinit var btnDepartureStationSelect: Button
    private lateinit var btnDepartureStationSelectText: String
    private lateinit var btnArrivalStationSelect: Button
    private lateinit var btnArrivalStationSelectText: String
    private lateinit var selectedDate: String
    private var selectedDepartureTimeStamp by Delegates.notNull<Long>()
    private var selectedArrivalTimeStamp by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 표준 함수 4가지
        // apply 자기자신 돌려줌 this
        btnDepartureOpenCalendar = findViewById<Button?>(R.id.btnOpenDepartureCalendar).apply {
            setOnClickListener(this@MainActivity)
        }
        // also 자기자신 돌려줌 this -> it
        btnArrivalOpenCalendar = findViewById<Button>(R.id.btnOpenArrivalCalendar).also {
            it.setOnClickListener(this)
        }

        // 함수안에서 돌려주는 값을 결정해줄것. 람다의 마지막은 리턴 값 , if 문 같은 경우 return@let it 사용
        btnDepartureStationSelect = findViewById<Button?>(R.id.btnDepartureStationSelect).let {
            it.setOnClickListener(this)
            it
//            return@let it
        }
        // 함수안에서 돌려주는 값을 결정. this를 반환
        btnArrivalStationSelect = findViewById<Button?>(R.id.btnArrivalStationSelect).run {
            setOnClickListener(this@MainActivity)
            this
        }

        val currentDate = getCurrentDate()
        selectedDepartureTimeStamp = getCurrentTimeStamp()
        selectedArrivalTimeStamp = getCurrentTimeStamp()
        btnDepartureOpenCalendarText = currentDate
        btnArrivalOpenCalendarText = currentDate

        btnDepartureOpenCalendarText = "출발일 : $btnDepartureOpenCalendarText"
        btnArrivalOpenCalendarText = "도착일 : $btnArrivalOpenCalendarText"

        btnDepartureOpenCalendar.setText(btnDepartureOpenCalendarText)
        btnArrivalOpenCalendar.setText(btnArrivalOpenCalendarText)
    }

    private val stationSelectActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedStation = data.getStringExtra("selectedStation")
                    btnDepartureStationSelectText =
                        data.getStringExtra(StationSelectActivity.DEPARTURE_STATION) ?: "출발역"
                    btnArrivalStationSelectText =
                        data.getStringExtra(StationSelectActivity.ARRIVAL_STATION) ?: "도착역"
//                    val isDeparture = data.getBooleanExtra("isDeparture", true)
//                    if (isDeparture) {
                    btnDepartureStationSelect.text = btnDepartureStationSelectText
//                    } else {
                    btnArrivalStationSelect.text = btnArrivalStationSelectText
//                    }
                }
            }
        }

    // DatePicker
    private val departureCalendarActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val data = result.data ?: return@registerForActivityResult

            selectedDepartureTimeStamp =
                data.getLongExtra(DepartureCalendarActivity.SELECTED_TIMESTAMP, 0)
            val selectedYear = data.getStringExtra(DepartureCalendarActivity.SELECTED_YEAR)
            val selectedMonth = data.getStringExtra(DepartureCalendarActivity.SELECTED_MONTH)
            val selectedDay = data.getStringExtra(DepartureCalendarActivity.SELECTED_DAY)
            val selectedDayOfWeek =
                data.getStringExtra(DepartureCalendarActivity.SELECTED_DAYOFWEEK)
            val selectedTime = data.getIntExtra(DepartureCalendarActivity.SELECTED_TIME, 0)
            if (selectedTime < 10) {
                btnDepartureOpenCalendarText =
                    "출발일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) 0${selectedTime}시 이후"
                btnDepartureOpenCalendar.text = btnDepartureOpenCalendarText
            } else {
                btnDepartureOpenCalendarText =
                    "출발일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) ${selectedTime}시 이후"
                btnDepartureOpenCalendar.text = btnDepartureOpenCalendarText
            }
        }

    private val arrivalCalendarActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedDateMillis = data?.getLongExtra("selectedDateMillis", 0)

                val selectedYear = data?.getStringExtra("selectedYear")
                val selectedMonth = data?.getStringExtra("selectedMonth")
                val selectedDay = data?.getStringExtra("selectedDay")
                val selectedDayOfWeek = data?.getStringExtra("selectedDayOfWeek")
                println(selectedYear + selectedMonth + selectedDay + selectedDayOfWeek)
                if (data != null) {

                    val selectedTime = data?.getLongExtra("selectedTime", 0)
                    if (selectedTime != null) {
                        if (selectedTime < 10) {
                            btnArrivalOpenCalendarText =
                                "도착일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) 0${selectedTime}시 이후"
                            btnArrivalOpenCalendar.text = btnArrivalOpenCalendarText
                        } else {
                            btnArrivalOpenCalendarText =
                                "도착일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) ${selectedTime}시 이후"
                            btnArrivalOpenCalendar.text = btnArrivalOpenCalendarText
                        }
                    }
                }
            }
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

    private fun getCurrentTimeStamp(): Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis
    }

    override fun onClick(v: View?) {
        when (v?.id ?: 0) {
            R.id.btnDepartureStationSelect,
            R.id.btnArrivalStationSelect -> {
                goStationSelectActivity()
            }

            R.id.btnOpenDepartureCalendar -> {
                goDepartureDateSelectActivity()
            }
            R.id.btnOpenArrivalCalendar -> {
                goArrivalDateSelectActivity()
            }
        }
    }

    fun goDepartureDateSelectActivity() {
        val departureCalendarIntent = Intent(this, DepartureCalendarActivity::class.java).apply {
            putExtra(
                DepartureCalendarActivity.DEPARTURE_DATE,
                selectedDepartureTimeStamp
            )
        }
        departureCalendarActivityResult.launch(departureCalendarIntent)
    }

    fun goArrivalDateSelectActivity() {
        val arrivalCalendarIntent = Intent(this, ArrivalCalendarActivity::class.java).apply {
            putExtra(
                ArrivalCalendarActivity.ARRIVALDATE,
                selectedArrivalTimeStamp
            )
        }
        arrivalCalendarActivityResult.launch(arrivalCalendarIntent)
    }

    fun goStationSelectActivity() {
        val stationSelectIntent = Intent(this, StationSelectActivity::class.java).apply {
            putExtra(
                StationSelectActivity.DEPARTURE_STATION,
                if (this@MainActivity::btnDepartureStationSelectText.isInitialized) {
                    btnDepartureStationSelectText
                } else {
                    "출발역"
                }
            )
            putExtra(
                StationSelectActivity.ARRIVAL_STATION,
                if (this@MainActivity::btnArrivalStationSelectText.isInitialized) {
                    btnArrivalStationSelectText
                } else {
                    "도착역"
                }
            )
        }
        // 출발 역 선택 플래그 설정 ( 출발역인가 true )
        stationSelectIntent.putExtra("isDeparture", true)
        stationSelectActivityResult.launch(stationSelectIntent)
    }
}