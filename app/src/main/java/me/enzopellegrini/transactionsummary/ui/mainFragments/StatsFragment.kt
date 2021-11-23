package me.enzopellegrini.transactionsummary.ui.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.databinding.FragmentStatsBinding
import me.enzopellegrini.transactionsummary.ui.CommonViewModel

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()
    private val activityViewModel: CommonViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val view = binding.root

        activityViewModel.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            if (!loggedIn) {
                findNavController().navigate(R.id.firebaseLogin)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}