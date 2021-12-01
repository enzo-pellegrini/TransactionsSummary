package me.enzopellegrini.transactionsummary.ui.stats

import android.graphics.RenderEffect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.databinding.FragmentStatsBinding
import me.enzopellegrini.transactionsummary.ui.CommonViewModel

@AndroidEntryPoint
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


        binding.categoryChart.let { chart ->
            chart.centerText = "Transactions by category"
            chart.setCenterTextSize(20.0F)
//            chart.setTransparentCircleAlpha(0) // Not working in dark mode
//            chart.setTransparentCircleColor(0);
            chart.setDrawCenterText(true)

            chart.legend.isEnabled = false
            chart.description.isEnabled = false

            chart.setTouchEnabled(false)
            chart.isRotationEnabled = false
            chart.isHighlightPerTapEnabled = false

            viewModel.pieData.observe(viewLifecycleOwner) { data ->
                chart.data = data
                chart.invalidate()
            }
        }


//        binding.accountChart.let { chart ->
//            viewModel.lineData.observe(viewLifecycleOwner) { data ->
//                chart.data = data
//                chart.invalidate()
//            }
//        }


        binding.accountChart.let { chart ->
            viewModel.lineData.observe(viewLifecycleOwner) { (data, labels) ->
                chart.data = data
                chart.setTouchEnabled(false)
                val xAxis = chart.xAxis
                xAxis.granularity = 1F
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return labels[value.toInt()]
                    }
                }

                binding.accountChart.invalidate()
            }
        }


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "StatsFragment"
    }
}