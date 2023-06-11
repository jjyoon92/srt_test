package com.jjyoon.calenderpractice

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jjyoon.calenderpractice.services.ApiResponse
import com.jjyoon.calenderpractice.services.SearchTrainApiService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
    private var selectedDepartureTime: Int = 0
    private var selectedArrivalTime: Int = 0

    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private lateinit var departureDate: String
    private lateinit var arrivalDate: String

    private lateinit var btnAdultMinus: Button
    private lateinit var btnAdultPlus: Button
    private lateinit var tvAdultCount: TextView
    private lateinit var btnChildMinus: Button
    private lateinit var btnChildPlus: Button
    private lateinit var tvChildCount: TextView
    private lateinit var btnOldMinus: Button
    private lateinit var btnOldPlus: Button
    private lateinit var tvOldCount: TextView

    private lateinit var btnSearchTrain: Button

    companion object RetrofitBuilder {
        var trainApiService: SearchTrainApiService

        init {
            val retrofit = Retrofit.Builder()
//                .baseUrl("http://172.30.1.23:4000")
//                .baseUrl("http://192.168.100.77:4000")
                .baseUrl("http://192.168.100.77:4001")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            trainApiService = retrofit.create(SearchTrainApiService::class.java)
        }
    }


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

        btnDepartureStationSelect.setOnClickListener {
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

        btnArrivalStationSelect.setOnClickListener {
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
            // 도착 역 선택 플래그 설정 ( 출발역인가 false )
            stationSelectIntent.putExtra("isDeparture", false)
            stationSelectActivityResult.launch(stationSelectIntent)
        }

        btnAdultMinus = findViewById<Button>(R.id.btnAdultMinus)
        btnAdultPlus = findViewById<Button>(R.id.btnAdultPlus)
        tvAdultCount = findViewById<TextView>(R.id.tvAdultCount)
        btnChildMinus = findViewById<Button>(R.id.btnChildMinus)
        btnChildPlus = findViewById<Button>(R.id.btnChildPlus)
        tvChildCount = findViewById<TextView>(R.id.tvChildCount)
        btnOldMinus = findViewById<Button>(R.id.btnOldMinus)
        btnOldPlus = findViewById<Button>(R.id.btnOldPlus)
        tvOldCount = findViewById<TextView>(R.id.tvOldCount)

        val adultCount = 1
        val childCount = 0
        val oldCount = 0

        val formattedAdultCount = getString(R.string.adult_count, adultCount)
        val formattedChildCount = getString(R.string.child_count, childCount)
        val formattedOldCount = getString(R.string.old_count, oldCount)

        tvAdultCount.text = formattedAdultCount
        tvChildCount.text = formattedChildCount
        tvOldCount.text = formattedOldCount

        setupCounterButton(btnAdultPlus, btnAdultMinus, tvAdultCount)
        setupCounterButton(btnChildPlus, btnChildMinus, tvChildCount)
        setupCounterButton(btnOldPlus, btnOldMinus, tvOldCount)


        btnSearchTrain = findViewById(R.id.btnSearchTrain)

        btnSearchTrain.setOnClickListener {
            println("열차 조회하기")
            sendRequestToSearchForTrains()
        }

        // 초기값
        btnDepartureStationSelectText = "동대구"
        btnArrivalStationSelectText = "수서"
        btnDepartureStationSelect.setText(btnDepartureStationSelectText)
        btnArrivalStationSelect.setText(btnArrivalStationSelectText)
        departureDate = dateFormat.format(getCurrentTimeStamp()).toString()
        arrivalDate = dateFormat.format(getCurrentTimeStamp()).toString()
    }


    private val stationSelectActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val data = result.data ?: return@registerForActivityResult
            btnDepartureStationSelectText =
                data.getStringExtra(StationSelectActivity.DEPARTURE_STATION) ?: "동대구"
            btnArrivalStationSelectText =
                data.getStringExtra(StationSelectActivity.ARRIVAL_STATION) ?: "수서"
            btnDepartureStationSelect.text = btnDepartureStationSelectText
            btnArrivalStationSelect.text = btnArrivalStationSelectText
        }

    // 출발 일정 선택 
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

            departureDate = dateFormat.format(selectedDepartureTimeStamp).toString()

            selectedDepartureTime = data.getIntExtra(DepartureCalendarActivity.SELECTED_TIME, 0)

            println("selectedDepartureTimeStamp : " + selectedDepartureTimeStamp)
            println("departureDate : " + departureDate)
            println("selectedDepartureTime : " + selectedDepartureTime)

            if (selectedDepartureTime < 10) {
                btnDepartureOpenCalendarText =
                    "출발일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) 0${selectedDepartureTime}시 이후"
                btnDepartureOpenCalendar.text = btnDepartureOpenCalendarText
            } else {
                btnDepartureOpenCalendarText =
                    "출발일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) ${selectedDepartureTime}시 이후"
                btnDepartureOpenCalendar.text = btnDepartureOpenCalendarText
            }
        }

    // 도착 일정 선택
    private val arrivalCalendarActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val data = result.data ?: return@registerForActivityResult

            selectedArrivalTimeStamp =
                data.getLongExtra(ArrivalCalendarActivity.SELECTED_TIMESTAMP, 0)
            val selectedYear = data.getStringExtra(ArrivalCalendarActivity.SELECTED_YEAR)
            val selectedMonth = data.getStringExtra(ArrivalCalendarActivity.SELECTED_MONTH)
            val selectedDay = data.getStringExtra(ArrivalCalendarActivity.SELECTED_DAY)
            val selectedDayOfWeek = data.getStringExtra(ArrivalCalendarActivity.SELECTED_DAYOFWEEK)

            arrivalDate = dateFormat.format(selectedArrivalTimeStamp).toString()

            selectedArrivalTime = data.getIntExtra(ArrivalCalendarActivity.SELECTED_TIME, 0)

            println("selectedArrivalTimeStamp : " + selectedArrivalTimeStamp)
            println("arrivalDate : " + arrivalDate)
            println("selectedArrivalTime : " + selectedArrivalTime)

            if (selectedArrivalTime < 10) {
                btnArrivalOpenCalendarText =
                    "도착일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) 0${selectedArrivalTime}시 이후"
                btnArrivalOpenCalendar.text = btnArrivalOpenCalendarText
            } else {
                btnArrivalOpenCalendarText =
                    "도착일 : ${selectedYear}년 ${selectedMonth}월 ${selectedDay}일(${selectedDayOfWeek}) ${selectedArrivalTime}시 이후"
                btnArrivalOpenCalendar.text = btnArrivalOpenCalendarText
            }
        }

    // BtnDatePicker 날짜, 요일, 시간 초기값 반환
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val date = dateFormat.format(calendar.time)
        val timeFormat = SimpleDateFormat("HH시", Locale.getDefault())
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
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

        selectedDepartureTime = currentTime
        selectedArrivalTime = currentTime

        return "${date}(${dayOfWeek}) ${time} 이후"
    }

    private fun getCurrentTimeStamp(): Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis
    }

    override fun onClick(v: View?) {
        when (v?.id ?: 0) {
//            R.id.btnDepartureStationSelect -> {
//                goStationSelectActivity()
//            }
//            R.id.btnArrivalStationSelect -> {
//                goStationSelectActivity()
//            }

            R.id.btnOpenDepartureCalendar -> {
                goDepartureDateSelectActivity()
            }

            R.id.btnOpenArrivalCalendar -> {
                goArrivalDateSelectActivity()
            }
        }
    }

    // 출발/도착 일정 
    fun goDepartureDateSelectActivity() {
        val departureCalendarIntent = Intent(this, DepartureCalendarActivity::class.java).apply {
            putExtra(
                DepartureCalendarActivity.DEPARTURE_DATE,
                selectedDepartureTimeStamp,
            )
            putExtra(
                DepartureCalendarActivity.SELECTED_TIME, selectedDepartureTime
            )
        }
        departureCalendarActivityResult.launch(departureCalendarIntent)
    }

    fun goArrivalDateSelectActivity() {
        val arrivalCalendarIntent = Intent(this, ArrivalCalendarActivity::class.java).apply {
            putExtra(
                ArrivalCalendarActivity.ARRIVAL_DATE, selectedArrivalTimeStamp
            )
            putExtra(
                ArrivalCalendarActivity.SELECTED_TIME, selectedArrivalTime
            )
        }
        arrivalCalendarActivityResult.launch(arrivalCalendarIntent)
    }

    // 인원수 설정
    fun setupCounterButton(btnPlus: Button, btnMinus: Button, tvCount: TextView) {
        btnPlus.setOnClickListener {
            var currentCount = tvCount.text.toString().toInt()
            if (currentCount < 10) {
                tvCount.text = (currentCount + 1).toString()
            }
        }

        btnMinus.setOnClickListener {
            var currentCount = tvCount.text.toString().toInt()
            if (currentCount > 0) {
                tvCount.text = (currentCount - 1).toString()
            }
        }
    }

    // 열차 조회 (서버에 요청)
    fun sendRequestToSearchForTrains() {
        val departureStation = btnDepartureStationSelectText
        val arrivalStation = btnArrivalStationSelectText
        val departureDate = departureDate
        val arrivalDate = arrivalDate
        val adultCount = tvAdultCount.text.toString().toInt()
        val childCount = tvChildCount.text.toString().toInt()
        val oldCount = tvOldCount.text.toString().toInt()

        val jsonObject = JSONObject()
        jsonObject.put("departStation", departureStation)
        jsonObject.put("arriveStation", arrivalStation)
        jsonObject.put("departTime", departureDate)
        jsonObject.put("adult", adultCount)
        jsonObject.put("kid", childCount)
        jsonObject.put("old", oldCount)

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val call = trainApiService.searchTrain(requestBody)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    // 서버 응답 처리
                    val apiResponse = response.body()
//                    println("response.message() : " + response.message())
                    // TODO: 서버 응답에 대한 로직 추가
                    println("요청 성공")
                    handleTrainResponse(apiResponse)
                } else {
                    // 서버 응답 실패
                    // TODO: 실패에 대한 처리 로직 추가
                    println("요청 실패")
                    handleTrainError()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // 요청 실패
                // TODO: 실패에 대한 처리 로직을 추가하세요.
                handleTrainError()
            }
        })
    }

    fun handleTrainResponse(apiResponse: ApiResponse?) {
        // TODO : 서버 응답에 대한 로직 구현
        apiResponse?.let {
//            val trainData = apiResponse.trainData
            // TODO : 응답으로 넘어온 데이터를 활용한 작업 수행
            println(apiResponse)
            println(apiResponse.result)
            println("apiResponse.data : " + apiResponse.data)
//            println(apiResponse.message)
        }
    }

    fun handleTrainError() {
        // TODO : 서버 응답 실패 또는 요청 실패에 대한 로직 처리 구현
        // ex: Error message, 재시도 ...
    }
}





































