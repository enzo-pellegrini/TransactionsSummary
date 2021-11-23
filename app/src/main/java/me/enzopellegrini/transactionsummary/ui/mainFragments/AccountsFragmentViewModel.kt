package me.enzopellegrini.transactionsummary.ui.mainFragments

import androidx.lifecycle.*
import com.plaid.internal.core.protos.link.workflow.primitives.SdkResult
import com.plaid.link.result.LinkResult
import com.plaid.link.result.LinkSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.enzopellegrini.transactionsummary.SingleLiveEvent
import me.enzopellegrini.transactionsummary.data.AccountsRepository
import me.enzopellegrini.transactionsummary.data.Item
import javax.inject.Inject

@HiltViewModel
class AccountsFragmentViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository
) : ViewModel() {

    // This apparently is the correct method, because it's possible by switching
    // between pages pretty fast to make the page crash (at least with log)
    val accounts = accountsRepository.accounts

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

    fun registerLinkResult(result: LinkSuccess): SingleLiveEvent<Boolean> {
        val out = SingleLiveEvent<Boolean>()
        accountsRepository.registerLinkResult(result).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                out.setValue(false)
            } else {
                out.setValue(true)
            }
        }
        return out
    }

    fun deleteItem(itemId: String) {
        accountsRepository.deleteItem(itemId)
    }
}
