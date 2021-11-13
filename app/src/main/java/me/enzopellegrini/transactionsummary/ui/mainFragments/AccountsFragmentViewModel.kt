package me.enzopellegrini.transactionsummary.ui.mainFragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.enzopellegrini.transactionsummary.SingleLiveEvent
import me.enzopellegrini.transactionsummary.data.AccountsRepository
import javax.inject.Inject

@HiltViewModel
class AccountsFragmentViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    fun getLinkToken(): SingleLiveEvent<Map<String, String>?> {
        val out = SingleLiveEvent<Map<String, String>?>()
        accountsRepository.getLinkToken().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                out.setValue(null)
            } else {
                out.setValue(task.result)
            }
        }
        return out
    }

    fun registerLinkResult(result: LinkSuccess) =
        accountsRepository.registerLinkResult(result)
}