package me.enzopellegrini.transactionsummary.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Transaction

class TransactionsAdapter(
    val all: List<Transaction>,
    val onClikListener: (Int) -> Unit,
    val stateFun: (TotalState) -> Unit
) : RecyclerView.Adapter<HomeViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
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

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
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