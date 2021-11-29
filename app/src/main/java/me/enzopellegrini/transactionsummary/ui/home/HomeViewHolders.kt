package me.enzopellegrini.transactionsummary.ui.home

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R

open class HomeViewHolder (val view: View) : RecyclerView.ViewHolder(view)

class TransactionViewHolder(view: View) : HomeViewHolder(view) {
    val nameView: TextView = view.findViewById(R.id.transaction_name)
    val amountView: TextView = view.findViewById(R.id.transaction_amount)
}

enum class TotalState {
    day,
    week,
    month
}

class TotalViewHolder(view: View, val stateFun: (TotalState) -> Unit) : HomeViewHolder(view) {
    var state = TotalState.day
    val leftArrow: ImageButton = view.findViewById(R.id.left_arrow)
    val rightArrow: ImageButton = view.findViewById(R.id.right_arrow)

    init {
        leftArrow.setOnClickListener {
            state = when (state) {
                TotalState.day -> TotalState.month
                TotalState.week -> TotalState.day
                TotalState.month -> TotalState.week
            }
            updateData()
        }
        rightArrow.setOnClickListener {
            state = when (state) {
                TotalState.day -> TotalState.week
                TotalState.week -> TotalState.month
                TotalState.month -> TotalState.day
            }
            updateData()
        }
        updateData()
    }

    private fun updateData() {
        stateFun(state)
    }
}
