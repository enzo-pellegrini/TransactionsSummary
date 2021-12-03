package me.enzopellegrini.transactionsummary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.enzopellegrini.transactionsummary.R

class TransactionSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View? = inflater.inflate(R.layout.transaction_sheet_content, container, false)


    companion object {
        const val TAG = "TransactionSheet"
    }
}