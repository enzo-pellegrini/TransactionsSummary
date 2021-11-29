package me.enzopellegrini.transactionsummary.ui.transactionPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.databinding.FragmentTransactionPageBinding

@AndroidEntryPoint
class transaction_page : Fragment() {
    private var _binding: FragmentTransactionPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionPageViewModel by viewModels()

    val args: transaction_pageArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionPageBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.getTransaction(args.transactionPosition).observe(viewLifecycleOwner) {
            binding.transactionName.text = it.name
            binding.transactionAmountConv.text = "${it.amount}$"
            binding.transactionDate.text = it.date.toString()
            binding.transactionCategory.text = "It has category of ${it.category_id}"
        }

        return view
    }
}