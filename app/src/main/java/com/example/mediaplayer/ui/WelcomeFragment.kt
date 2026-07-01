package com.example.mediaplayer.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentWelcomeBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

/*
    *   piccola spiegazione di cosa si sta facendo qui :
    *   stiamo effettuando il biding tra questa classe e il file XML inerente alla Welcome,
    *   in modo da poter accedere in modo rapido agli elementi grafici.
    *
    *   guardare le esercitazioni: navigation
    *   siti visionati :
    *   https://stackoverflow.com/questions/62952957/viewbinding-in-fragment
    *   https://www.geeksforgeeks.org/android/data-binding-in-android-activities-views-and-fragments/
    *
    *
*/

class WelcomeFragment : Fragment() {
    private var _biding: FragmentWelcomeBinding? = null
    private val biding get() = _biding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _biding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return biding.root
    }

    /*
        Laboratorio FireBase
    */

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract())
    { res ->
        this.onSignInResult(res)
    }


    private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult?) {
        val response = res?.idpResponse
        if (res?.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            val intent = Intent(requireContext(), LibraryFragment::class.java)
            intent.putExtra("user_uid", user?.uid)

            startActivity(intent)
            activity?.finish()
        } else {
            Log.e("FirebaseLogin", "Login Error! ${response?.error?.errorCode}")
        }
    }

    override fun onDestroyView() {
        _biding = null
        super.onDestroyView()
    }
}