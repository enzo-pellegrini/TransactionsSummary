package me.enzopellegrini.transactionsummary.ui.login

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.enzopellegrini.transactionsummary.databinding.FirebaseLoginFragmentBinding
import me.enzopellegrini.transactionsummary.ui.StartActivity

@AndroidEntryPoint
class FirebaseLogin : Fragment() {


    private val viewModel: FirebaseLoginViewModel by viewModels()
    private var _binding: FirebaseLoginFragmentBinding? = null
    private val binding get() = _binding!!

    private val signinLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FirebaseLoginFragment", "In the onCreate of login fragment")
        _binding = FirebaseLoginFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.loginButton.setOnClickListener {
            login()
        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().popBackStack()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        (activity as StartActivity).nav.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as StartActivity).nav.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as StartActivity).nav.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun login() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        val signinIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signinLauncher.launch(signinIntent)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode != RESULT_OK) {
            Snackbar.make(binding.loginButton, "Login failed", Snackbar.LENGTH_SHORT).show()
        }
    }
}