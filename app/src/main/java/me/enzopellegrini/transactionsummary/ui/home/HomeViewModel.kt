package me.enzopellegrini.transactionsummary.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository
) : ViewModel() {
    val transactions = transactionsRepository.transactions

    private val _totalState = MutableLiveData(TotalState.Day)
    val totalState: LiveData<TotalState> = _totalState

    val totalValue: LiveData<Double> =
        Transformations.switchMap(totalState) {
            transactionsRepository.amountSince(it)
        }


    val totalInterface = object : TotalInterface {
        override fun rightAction() {
            _totalState.value = when (totalState.value) {
                TotalState.Day -> TotalState.Week
                TotalState.Week -> TotalState.Month
                else -> totalState.value
            }
        }
        override fun leftAction() {
            _totalState.value = when (totalState.value) {
                TotalState.Week -> TotalState.Day
                TotalState.Month -> TotalState.Week
                else -> totalState.value
            }
        }

        override val state get() = totalState.value!!
        override val total get() = totalValue.value!!
    }

}

interface TotalInterface {
    fun rightAction()
    fun leftAction()
    val state: TotalState
    val total: Double
}

enum class TotalState {
    Day,
    Week,
    Month
}