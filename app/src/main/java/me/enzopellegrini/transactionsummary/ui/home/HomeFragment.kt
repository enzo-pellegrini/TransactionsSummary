package me.enzopellegrini.transactionsummary.ui.home

import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.data.Transaction
import me.enzopellegrini.transactionsummary.databinding.FragmentHomeBinding
import me.enzopellegrini.transactionsummary.ui.CommonViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Navigate to the login fragment if not logged in
        commonViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (!it) navigateToLogin()
        }


        commonViewModel.hasAccounts.observe(viewLifecycleOwner) {
            if (it) {
                binding.goToAccounts.visibility = INVISIBLE
                binding.transactionsList.visibility = VISIBLE
                viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                    setupRecyclerView(transactions)
                }
            } else {
                binding.transactionsList.visibility = INVISIBLE
                binding.goToAccounts.visibility = VISIBLE
            }
        }

        binding.goToAccounts.setOnClickListener {
            findNavController().navigate(R.id.navigation_accounts)
        }


        viewModel.categoriesSelected.observe(viewLifecycleOwner) { categories ->
            setupFilterGroup(categories)
        }

        val testSheet = TransactionSheet()
        activity?.let { testSheet.show(it.supportFragmentManager, TransactionSheet.TAG) }

        return binding.root
    }



    private fun setupRecyclerView(all: List<Transaction>) {
        val adapter = TransactionsAdapter(all) {
            val action = HomeFragmentDirections
                .actionNavigationHomeToTransactionPage(it)
            findNavController().navigate(action)
        }

        binding.transactionsList.adapter = adapter
        binding.transactionsList.layoutManager = LinearLayoutManager(context)
    }

    private fun setupFilterGroup(categoryMap: Map<String, LiveData<Boolean>>) {
        binding.filterChipGroup.removeAllViews()

        categoryMap.forEach { (categoryName, status) ->
            val chip = Chip(context)
            chip.text = categoryName
            chip.isCheckable = true
            status.observe(viewLifecycleOwner) {
                chip.isChecked = it
            }
            chip.id = generateViewId()

            chip.setOnClickListener {
                viewModel.setCategorySelected(categoryName, chip.isChecked)
            }

            binding.filterChipGroup.addView(chip)
        }
    }

    private fun navigateToLogin() {
        Log.d("HomeFragment", "Navigating to login fragment")
        findNavController().navigate(R.id.firebaseLogin)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}