package me.enzopellegrini.transactionsummary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Transaction

class TransactionSheet(val transaction: Transaction) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.transaction_sheet_content, container, false)



        return view
    }


    companion object {
        const val TAG = "TransactionSheet"
    }
}