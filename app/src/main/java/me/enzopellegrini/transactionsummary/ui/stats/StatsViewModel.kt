package me.enzopellegrini.transactionsummary.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.model.FieldIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.SegmentSummary
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


    val pieData = Transformations.map(transactionRepository.summary) { summary ->
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
        set.colors = ColorTemplate.PASTEL_COLORS.toList()

        PieData(set)
    }


}