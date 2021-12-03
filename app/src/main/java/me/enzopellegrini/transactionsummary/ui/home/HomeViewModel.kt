package me.enzopellegrini.transactionsummary.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.AccountsRepository
import me.enzopellegrini.transactionsummary.data.Transaction
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import me.enzopellegrini.transactionsummary.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    transactionsRepository: TransactionRepository,
    accountsRepository: AccountsRepository,
    userRepository: UserRepository
) : ViewModel() {
    val isLoggedIn = userRepository.isLoggedIn
    val hasAccounts = accountsRepository.hasAccounts

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
        transactionsRepository.transactions.map { categories ->
            categories
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


}