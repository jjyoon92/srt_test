package com.jjyoon.calenderpractice

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jjyoon.calenderpractice.adapters.trainScheduleAdapter
import com.jjyoon.calenderpractice.services.SearchTrainScheduleApiService
import com.jjyoon.calenderpractice.services.TrainItem
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar

class TrainScheduleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: trainScheduleAdapter
    private lateinit var btnNextDay: Button
    private lateinit var btnPreviousDay: Button
    private lateinit var tvTrainScheduleDate: TextView

    private var departureStation: String? = null
    private var arrivalStation: String? = null
    private var departureDate: String? = null
    private var departureTime: Int? = null
    private var returnDate: String? = null
    private var returnTime: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_schedule)

        btnNextDay = findViewById(R.id.btnTrainScheduleNextDay)
        btnPreviousDay = findViewById(R.id.btnTrainSchedulePreviousDay)
        tvTrainScheduleDate = findViewById(R.id.tvTrainScheduleDate)

        // MainActivity에서 넘어온 선택된 데이터
        departureStation = intent.getStringExtra("departureStation")
        arrivalStation = intent.getStringExtra("arrivalStation")
        departureDate = intent.getStringExtra("departureDate")
        departureTime = intent.getIntExtra("departureTime", 0)
        returnDate = intent.getStringExtra("returnDate")
        returnTime = intent.getStringExtra("returnTime")

        // api 요청으로 받아온 데이터
        val result = intent.getStringExtra("RESULT")
        val dataString = intent.getStringExtra("DATA")

        // 날짜 형식 변경
        val originalFormat = SimpleDateFormat("yyyyMMdd")
        val targetFormat = SimpleDateFormat("MM월 dd일")
        val date = originalFormat.parse(departureDate)
        val formattedDate = targetFormat.format(date)

        tvTrainScheduleDate.text = formattedDate

        println("departureStation : $departureStation")
        println("arrivalStation : $arrivalStation")
        println("departureDate : $departureDate")
        println("departureTime : $departureTime")
        println("returnDate : $returnDate")
        println("returnTime : $returnTime")

        val seatGradeSelectRadioGroup = findViewById<RadioGroup>(R.id.radioGroupSeatGradeSelect)
        val radioBtnSpecialSeatSelect = findViewById<RadioButton>(R.id.radioBtnSpecialSeatSelect)
        val radioBtnCommonSeatSelect = findViewById<RadioButton>(R.id.radioBtnCommonSeatSelect)
        val trainScheduleDropdownMenu = findViewById<LinearLayout>(R.id.trainScheduleDropdownMenuLl)

//        trainScheduleDropdownMenu.visibility = View.GONE

//        radioBtnSpecialSeatSelect.setOnClickListener {
//            trainScheduleDropdownMenu.visibility = View.VISIBLE
//        }
//
//        radioBtnCommonSeatSelect.setOnClickListener {
//            trainScheduleDropdownMenu.visibility = View.VISIBLE
//        }


        // DATA 문자열을 다시 List<TrainItem>으로 변환
        val gson = Gson()
        val listType = object : TypeToken<List<TrainItem>>() {}.type
        val data: List<TrainItem> = gson.fromJson(dataString, listType)

        val itemDecorator = VerticalSpacingItemDecorator(20)

        recyclerView = findViewById(R.id.trainTimeRecyclerView)
        adapter = trainScheduleAdapter()

        val filteredData = data.filter { item ->
            val trainDateTimeFormat = SimpleDateFormat("yyyyMMddHHmmss")
            val trainDateTime = trainDateTimeFormat.parse(item.date + item.depPlandTime)

            // 지정된 시간 설정
            val specificHour = departureTime
            val specificDateTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, specificHour!!)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time

            // 현재 시간 설정
            val currentDateTime = Calendar.getInstance().time

            // 현재 시간과 지정한 시간 중 늦은 시간을 기준으로 선택
            val latestDateTime = if (specificDateTime.after(currentDateTime)) specificDateTime else currentDateTime

            if (trainDateTime.after(latestDateTime)) {
                // 기준 시간 이후의 데이터만 허용
                true
            } else {
                // 기준 시간 이전의 데이터는 필터링
                false
            }
        }

        adapter.setData(filteredData) // 어댑터에 데이터 설정

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(itemDecorator)
        recyclerView.layoutManager = LinearLayoutManager(this)



    }

    fun searchTrainScheduleForNextDay() {
        // 다음날 기차 스케줄 검색
//        val currentDate = SimpleDateFormat("yyyyMMdd").parse(departureDate)
        val calendar = Calendar.getInstance()
//        calendar.time = currentDate
        calendar.add(Calendar.DATE, 1)
        val nextDate = SimpleDateFormat("yyyyMMdd").format(calendar.time)

        // 새로운 날짜 표시

    }
}