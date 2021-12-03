package me.enzopellegrini.transactionsummary.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.R
import me.enzopellegrini.transactionsummary.databinding.FragmentStatsBinding

@AndroidEntryPoint
class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val view = binding.root


        viewModel.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            if (!loggedIn) {
                findNavController().navigate(R.id.firebaseLogin)
            }
        }


        viewModel.hasAccounts.observe(viewLifecycleOwner) {
            if (it) {
                binding.goRightText.visibility = INVISIBLE
                binding.goToAccounts.visibility = INVISIBLE
                binding.categoryChart.visibility = VISIBLE
                binding.accountChart.visibility = VISIBLE

                viewModel.pieData.observe(viewLifecycleOwner) { data ->
                    setupCategoryChart(data)
                }

                viewModel.lineData.observe(viewLifecycleOwner) { (data, labels) ->
                    setupAccountChart(data, labels)
                }
            }
            else {
                binding.goRightText.visibility = VISIBLE
                binding.goToAccounts.visibility = VISIBLE
                binding.categoryChart.visibility = INVISIBLE
                binding.accountChart.visibility = INVISIBLE
            }
        }

        binding.goToAccounts.setOnClickListener {
            findNavController().navigate(R.id.navigation_accounts)
        }


        return view
    }

    private fun setupCategoryChart(data: PieData) {
        binding.categoryChart.let { chart ->

            chart.centerText = "Transactions by category"
            chart.setCenterTextSize(20.0F)
    //            chart.setTransparentCircleAlpha(0) // Not working in dark mode
    //            chart.setTransparentCircleColor(0);
            chart.setDrawCenterText(true)
            chart.setDrawEntryLabels(false)
            chart.setNoDataText("This chart has no data")

            chart.legend.isEnabled = true
            chart.description.isEnabled = false

            chart.setTouchEnabled(false)
            chart.isRotationEnabled = false
            chart.isHighlightPerTapEnabled = false

            val legend = chart.legend
            legend.isWordWrapEnabled = true


            chart.data = data
            chart.invalidate()
        }
    }


    private fun setupAccountChart(data: LineData, labels: List<String>) {
        binding.accountChart.let { chart ->

            chart.data = data
            chart.setTouchEnabled(false)
            val xAxis = chart.xAxis
            xAxis.granularity = 1F
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return labels[value.toInt() % labels.size]
                }
            }
            chart.setDrawGridBackground(false)
            xAxis.setDrawGridLines(false)
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawAxisLine(false)

            chart.description.isEnabled = false
            chart.setDrawGridBackground(false)


            binding.accountChart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "StatsFragment"
    }
}