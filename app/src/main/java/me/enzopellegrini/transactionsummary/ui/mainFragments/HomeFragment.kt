package me.enzopellegrini.transactionsummary.ui.mainFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
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
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (!isLoggedIn) {
                Log.d("HomeFragment", "Navigating to login fragment")
                findNavController().navigate(R.id.firebaseLogin)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}