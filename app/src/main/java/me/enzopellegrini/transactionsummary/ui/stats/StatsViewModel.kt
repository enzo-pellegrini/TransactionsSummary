package me.enzopellegrini.transactionsummary.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.firebase.firestore.model.FieldIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.SegmentSummary
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {

    val pieData = Transformations.map(transactionRepository.summary) { summary ->
        summary
            .toList()
            .map { p -> ValueDataEntry(
                p.first,
                p.second.sum
            ) }
    }


}