package me.enzopellegrini.transactionsummary.ui.stats

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.SegmentSummary
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {


    val pieData = Transformations.map(transactionRepository.categorySummary) { summary ->
        // Weird api
        val set = PieDataSet(summary
            .toList()
            .sortedByDescending { it.second.sum } // It looks better
            .map {
                PieEntry (
                    it.second.sum.toFloat(),
                    it.first
                )
            },
            "Transactions by category")
        set.colors = ColorTemplate.MATERIAL_COLORS.toList()
        set.sliceSpace = 2.0F
        set.setDrawValues(false)

        PieData(set)
    }


//    val lineData = Transformations.map(transactionRepository.summaryByDayPerAccount) { summary ->
//        val dates: List<Date> = summary.map { it.first }
//
//    }

    val lineData = Transformations.map(transactionRepository.summaryPerAccountPerDay) { summary ->
        // Get all the dates present for all the accounts
        val dates: List<Date> = summary.values.toList().flatMap { it.keys }.sorted().distinct()

        // Create a map in map (almost) of institution name to
        // list of x value (corresponding to the date) to y value
        // (corresponding to the total)
        val lineSets: List<Pair<String, List<Pair<Int, Double>>>>  = summary.map { accountMap -> Pair(
            accountMap.key,
            dates.mapIndexed { index, date ->
                if (accountMap.value.containsKey(date)) Pair(index, accountMap.value[date]?.sum!!) else Pair(index, 0.0)
            }
        ) }

        // Convert the last map of maps to a list of LineDataSet, and set color
        val lineDataSets = lineSets.mapIndexed { index, it ->
            val out = LineDataSet(
                it.second.map { pair -> Entry (
                    pair.first.toFloat(),
                    pair.second.toFloat()
                ) },
                it.first
            )
            out.color = ColorTemplate.MATERIAL_COLORS[index%ColorTemplate.COLORFUL_COLORS.size]

            out
        }

        val labels = dates.map {
            val c = Calendar.getInstance()
            c.time = it
            "${c.get(Calendar.MONTH)} ${c.get(Calendar.DAY_OF_MONTH)}"
        }


        Pair(LineData(lineDataSets), labels)
    }



    companion object {
        const val TAG = "StatsViewModel"
    }
}