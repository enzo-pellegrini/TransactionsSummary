package me.enzopellegrini.transactionsummary.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Transaction

class TransactionsAdapter(
    private val all: List<Transaction>,
    val onClickListener: (Transaction) -> Unit,
) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.transaction_name)
        val amountView: TextView = view.findViewById(R.id.transaction_amount)
        val categoryView: TextView = view.findViewById(R.id.transaction_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        all[position].let {
            holder.nameView.text = it.name
            holder.amountView.text = "${it.amount}$"
            holder.categoryView.text = it.category
        }

        holder.view.setOnClickListener {
            onClickListener(all[position])
        }
    }

    override fun getItemCount() = all.size
}