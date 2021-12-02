package me.enzopellegrini.transactionsummary.ui.home

import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.Transaction
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository
) : ViewModel() {
//    val transactions = transactionsRepository.transactions
    val transactions = transactionsRepository.transactions.switchMap {
        filterTransactions(it)
    }


    private val _filteredOutCategories: MutableLiveData<MutableSet<String>> = MutableLiveData(mutableSetOf())

    private fun isCategorySelected(c: String): LiveData<Boolean> =
        _filteredOutCategories.map { !it.contains(c) }

    private fun filterTransactions(ts: List<Transaction>): LiveData<List<Transaction>> =
        _filteredOutCategories.map { excludedCategories ->
            ts.filter { transaction ->
                !excludedCategories.contains( transaction.category )
            }
        }


    val categoriesSelected: LiveData<Map<String, LiveData<Boolean>>> =
        transactionsRepository.transactions.map {
            it
                .map { it.category }.distinct()
                .map {
                    Pair(it, isCategorySelected(it))
                }
                .toMap()
        }

    fun setCategorySelected(category: String, selected: Boolean) {
        if (selected) {
            _filteredOutCategories.value?.remove(category)
        }
        else {
            _filteredOutCategories.value?.add(category)
        }
        _filteredOutCategories.value = _filteredOutCategories.value
    }

    // Shit for the useless summary
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