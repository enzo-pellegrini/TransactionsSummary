package me.enzopellegrini.transactionsummary.ui.accounts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Item


class AccountsAdapter(val data: List<Item>, val onClick: (String)->Unit) : RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.account_name)
        val deleteButton: Button = view.findViewById(R.id.delete_account)
        val shareButton: Button = view.findViewById(R.id.share_account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = data[position].institution_name
        holder.deleteButton.setOnClickListener { onClick(data[position].item_id) }

        holder.shareButton.setOnClickListener {

        }
//        card.isChecked = true // Should decide if I want to do this
//        card.setOnClickListener {
//            card.isChecked = !card.isChecked
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
