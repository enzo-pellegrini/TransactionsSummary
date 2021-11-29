package me.enzopellegrini.transactionsummary.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Transaction

class TransactionsAdapter(
    val all: List<Transaction>,
    val onClikListener: (Int) -> Unit,
    val stateFun: (TotalState) -> Unit
) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    open class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class TransactionViewHolder(view: View) : ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.transaction_name)
        val amountView: TextView = view.findViewById(R.id.transaction_amount)
    }

    enum class TotalState {
        day,
        week,
        month
    }

    class TotalViewHolder(view: View, val stateFun: (TotalState) -> Unit) : ViewHolder(view) {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 2) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_item, parent, false)

            return TransactionViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.total_item, parent, false)

            return TotalViewHolder(view, stateFun)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is TransactionViewHolder -> {
                holder.nameView.text = all[position - 1].name
                holder.amountView.text = "${all[position - 1].amount}$"

                holder.view.setOnClickListener {
                    onClikListener(position - 1)
                }
            }
            else -> {

            }
        }
    }

    override fun getItemCount() =
        all.size + 1


    override fun getItemViewType(position: Int) =
        if (position == 0) 1 else 2
}