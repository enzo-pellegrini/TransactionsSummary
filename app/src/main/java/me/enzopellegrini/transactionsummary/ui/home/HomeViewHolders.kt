package me.enzopellegrini.transactionsummary.ui.home

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R

open class HomeViewHolder (val view: View) : RecyclerView.ViewHolder(view)

class TransactionViewHolder(view: View) : HomeViewHolder(view) {
    val nameView: TextView = view.findViewById(R.id.transaction_name)
    val amountView: TextView = view.findViewById(R.id.transaction_amount)
}


class TotalViewHolder(view: View, val statusInterface: TotalInterface) : HomeViewHolder(view) {
    val leftButton: ImageButton = view.findViewById(R.id.left_arrow)
    val rightButton: ImageButton = view.findViewById(R.id.right_arrow)
    val totalText: TextView = view.findViewById(R.id.total_amount)

    init {
        leftButton.setOnClickListener {
            statusInterface.leftAction()
        }

        rightButton.setOnClickListener {
            statusInterface.rightAction()
        }

        notifyState(statusInterface.state)
    }

    fun notify(data: Pair<Double, TotalState>) {
        notifyState(data.second)
        notifyTotal(data.first)
    }

    private fun notifyTotal(amount: Double) {
        totalText.text = "${amount}$"
    }

    fun notifyState(newState: TotalState) {
        if (newState == TotalState.Day)
            leftButton.visibility = INVISIBLE
        else
            leftButton.visibility = VISIBLE


        if (newState == TotalState.Month)
            rightButton.visibility = INVISIBLE
        else
            rightButton.visibility = VISIBLE

    }
}
