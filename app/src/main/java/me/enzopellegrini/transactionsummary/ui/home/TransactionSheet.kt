package me.enzopellegrini.transactionsummary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.enzopellegrini.transactionsummary.data.Transaction
import me.enzopellegrini.transactionsummary.databinding.TransactionSheetContentBinding
import me.enzopellegrini.transactionsummary.toDollars

class TransactionSheet(private val transaction: Transaction) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        val binding = TransactionSheetContentBinding.inflate(inflater, container, false)

        binding.transactionName.text = transaction.name
        binding.transactionAmount.text  = transaction.amount.toDollars()
        binding.transactionCategory.text = transaction.category

        return binding.root
    }


    companion object {
        const val TAG = "TransactionSheet"
    }
}