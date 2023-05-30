package com.jjyoon.calenderpractice.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jjyoon.calenderpractice.R

class TimeSelectAdapter(
    private val context: Context,
    private val times: List<Int>,
    private val onTimeClickListener: OnTimeClickListener
) : RecyclerView.Adapter<TimeSelectAdapter.TimeViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION // 이전에 선택한 버튼의 위치를 추적하기 위한 변수
    private var recyclerView: RecyclerView? = null

    // ViewHolder 클래스 정의
    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val buttonItem: Button = itemView.findViewById(R.id.buttonItem)
        private val departureRecyclerView: RecyclerView? = (context as Activity).findViewById(R.id.departureTimeSelectRecyclerView)
        private val arrivalRecyclerView: RecyclerView? = (context as Activity).findViewById(R.id.departureTimeSelectRecyclerView)


        fun bind(time: Int) {
            if (time < 10) {
                buttonItem.text = "0${time}시"
            } else {
                buttonItem.text = "${time}시"
            }

            buttonItem.setOnClickListener {
                // 버튼 클릭 시 동작할 코드
                val clickedPosition = adapterPosition
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    updateButtonColor(clickedPosition)
                    onTimeClickListener.onTimeClick(time)
//                    scrollSelectedItemToCenter(clickedPosition)
                }
            }

            // 이전에 선택한 버튼인 경우 색상을 업데이트
            if (adapterPosition == selectedPosition) {
                changeButtonColor(buttonItem, isSelected = true)
            } else {
                changeButtonColor(buttonItem, isSelected = false)
            }

            // viewtreeObserver ?
            buttonItem.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    val layoutManager = departureRecyclerView?.layoutManager as? LinearLayoutManager
                    val offset = (layoutManager?.width ?: 0) / 2 - (buttonItem.width / 2)

                    layoutManager?.scrollToPositionWithOffset(selectedPosition, offset)

                    buttonItem.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                    // 버튼을 가운데로 정렬
//                    val layoutPrams = buttonItem.layoutParams as ViewGroup.MarginLayoutParams
//                    val parentWidth = (recyclerView?.parent as? View)?.width
//                    val buttonWidth = buttonItem.measuredWidth // <- 이거 확인 viewtreeObserver 아마도 0일 것임.
//
//                    layoutPrams.marginStart = (parentWidth - buttonWidth) / 2
//                    buttonItem.layoutParams = layoutPrams
                }
            })

            buttonItem.post {
                Log.d("버튼너비" , buttonItem.width.toString())
            }
        }
    }


    // 스크롤할 아이템을 가운데로 정렬하는 함수
//    private fun scrollSelectedItemToCenter(selectedPosition: Int) {
//        val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
//        val offset = (recyclerView?.width ?: 0) / 2 - ( / 2)
//
//        layoutManager?.scrollToPositionWithOffset(selectedPosition, offset)
//    }

    // 버튼의 색상을 변경하는 함수
    private fun changeButtonColor(button: Button, isSelected: Boolean) {
        val color = if (isSelected) {
            Color.rgb(224, 212, 255)
        } else {
            // 선택하지 않은 버튼의 색상
            Color.TRANSPARENT
        }
        button.setBackgroundColor(color)
    }

    // 버튼의 색상을 업데이트하는 함수
    private fun updateButtonColor(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position // 선택한 버튼의 위치 업데이트

        if (previousPosition != RecyclerView.NO_POSITION) {
            // 이전에 선택한 버튼이 있는 경우에만 해당 버튼의 색상 초기화
            notifyItemChanged(previousPosition)
        }

        if (selectedPosition != RecyclerView.NO_POSITION) {
            // 선택한 버튼의 색상 변경
            notifyItemChanged(selectedPosition)
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    // 숫자 클릭 콜백을 정의하는 인터페이스
    interface OnTimeClickListener {
        fun onTimeClick(time: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val time = times[position]
        holder.bind(time)
    }

    override fun getItemCount(): Int {
        return times.size
    }


}