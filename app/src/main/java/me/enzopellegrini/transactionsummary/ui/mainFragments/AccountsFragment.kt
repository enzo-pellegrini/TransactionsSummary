package me.enzopellegrini.transactionsummary.ui.mainFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.plaid.link.OpenPlaidLink
import com.plaid.link.PlaidActivityResultContract
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.databinding.FragmentAccountsBinding


@AndroidEntryPoint
class AccountsFragment : Fragment() {
    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountsFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.addAccountButton.setOnClickListener {
            addAccount()
        }

        return view
    }

    @OptIn(PlaidActivityResultContract::class)
    private val linkAccountToPlaid =
        registerForActivityResult(OpenPlaidLink()) {
            when (it) {
                is LinkSuccess -> {
                    Log.d("LoginFragment", "public token: ${it.publicToken}")
                    // Pass the Link Result to the view model
                    viewModel.registerLinkResult(it)

                    // Show the user some respect
                    Snackbar.make(binding.coordinator, "Successfully added account", Snackbar.LENGTH_LONG)
                        .show()
                }
                is LinkExit -> {
                    if (it.error != null) Log.e("LoginFragment", it.error!!.errorMessage)
                    Snackbar.make(binding.coordinator, "Failed adding bank account", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

    private fun addAccount() {
        viewModel.getLinkToken().observe(this) { linkToken ->
            if (linkToken == null) {
                Snackbar.make(binding.coordinator, "Failed getting link token", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                val linkTokenConfiguration = linkTokenConfiguration {
                    token = linkToken["link_token"]
                }
                linkAccountToPlaid.launch(linkTokenConfiguration)
            }
        }
    }
}