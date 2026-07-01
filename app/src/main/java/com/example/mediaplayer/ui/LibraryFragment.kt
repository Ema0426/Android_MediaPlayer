package com.example.mediaplayer.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentLibraryBinding
import com.example.mediaplayer.databinding.FragmentWelcomeBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LibraryFragment : Fragment() {
    private var _biding : FragmentLibraryBinding? = null
    private val biding get() = _biding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _biding = FragmentLibraryBinding.inflate(inflater, container, false)
        return biding.root
    }

    override fun onDestroyView() {
        _biding = null
        super.onDestroyView()
    }
}