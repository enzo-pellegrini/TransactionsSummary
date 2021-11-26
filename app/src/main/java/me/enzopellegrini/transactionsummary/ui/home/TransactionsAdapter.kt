package me.enzopellegrini.transactionsummary.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Transaction

class TransactionsAdapter(val all: List<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.transaction_name)
        val amountView: TextView = view.findViewById(R.id.transaction_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameView.text = all[position].name
        holder.amountView.text = "${all[position].amount}$"
    }

    override fun getItemCount() =
        all.size
}