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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.databinding.FragmentHomeBinding
import me.enzopellegrini.transactionsummary.ui.CommonViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val commonViewModel: CommonViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Navigate to the login fragment if not logged in
        commonViewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (!isLoggedIn) {
                Log.d("HomeFragment", "Navigating to login fragment")
                findNavController().navigate(R.id.firebaseLogin)
            }
        }


        var oldObserver: ((TotalState) -> Unit)? = null
        commonViewModel.hasAccounts.observe(viewLifecycleOwner) {
            if (it) {
                binding.goToAccounts.visibility = INVISIBLE
                binding.transactionsList.let { rv ->
                    viewModel.transactions.observe(viewLifecycleOwner) { all ->
                        val adapter = TransactionsAdapter(all, {
                            val action = HomeFragmentDirections
                                .actionNavigationHomeToTransactionPage(it)
                            findNavController().navigate(action)
                        },
                        totalInterface = viewModel.totalInterface)

                        rv.adapter = adapter
                        rv.layoutManager = LinearLayoutManager(context)

                        oldObserver?.let { viewModel.totalState.removeObserver(it) }
                        oldObserver = {
                            adapter.notifyTotalStateChange( Pair(
                                viewModel.totalValue.value?:0.0,
                                it
                            ))
                        }
                        oldObserver?.let {
                            viewModel.totalState.observe(viewLifecycleOwner, it)
                        }
                    }
                }
            } else {
                binding.goToAccounts.visibility = VISIBLE
            }
        }

        binding.goToAccounts.setOnClickListener {
            findNavController().navigate(R.id.navigation_accounts)
        }


//        val chip = Chip(context)
//        chip.text = "This is some text"
//        chip.isChecked = true
//        chip.id = generateViewId()
//
//        binding.filterChipGroup.addView(chip)
        viewModel.categoriesSelected.observe(viewLifecycleOwner) {
            binding.filterChipGroup.removeAllViews()

            it.forEach { (categoryName, status) ->
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


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}