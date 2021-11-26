package me.enzopellegrini.transactionsummary.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

//        if (commonViewModel.hasAccounts()) {
//        binding.transactionsList.let {
//            it.adapter = TransactionsAdapter()
//            it.layoutManager = LinearLayoutManager(context)
//        }
        commonViewModel.hasAccounts.observe(viewLifecycleOwner) {
            if (it) {
                binding.goToAccounts.visibility = INVISIBLE
                binding.transactionsList.let { rv ->
                    viewModel.transactions.observe(viewLifecycleOwner) {
                        rv.adapter = TransactionsAdapter(it!!)
                        rv.layoutManager = LinearLayoutManager(context)
                    }
                }
            } else {
                binding.goToAccounts.visibility = VISIBLE
            }
        }
        binding.goToAccounts.setOnClickListener {
            findNavController().navigate(R.id.navigation_accounts)
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}