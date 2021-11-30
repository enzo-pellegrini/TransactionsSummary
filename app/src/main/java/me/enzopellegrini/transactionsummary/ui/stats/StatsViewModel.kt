package me.enzopellegrini.transactionsummary.ui.stats

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {

//    val pieData = Transformations.map(transactionRepository.summary) { summary ->
//        summary
//            .toList()
//            .map { p -> ValueDataEntry(
//                p.first,
//                p.second.sum
//            ) }
//    }


    val pieData = Transformations.map(transactionRepository.categorySummary) { summary ->
        // Weird api
        val set = PieDataSet(summary
            .toList()
            .map {
                PieEntry (
                    it.second.sum.toFloat(),
                    it.first
                )
            },
            "Transactions by category")
        set.colors = ColorTemplate.JOYFUL_COLORS.toList()
        set.sliceSpace = 2.0F

        PieData(set)
    }


//    val lineData = Transformations.map(transactionRepository.itemSummary) { summary ->
//        val set
//    }

}