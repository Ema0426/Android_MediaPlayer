package com.example.mediaplayer

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.service.PlaybackService
import com.example.mediaplayer.viewmodel.MusicViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.firebase.ui.auth.AuthUI
import android.widget.ImageView
import coil.load
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

fun Fragment.showLogoutDialog(viewModel: MusicViewModel) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle("Disconnessione")
        .setMessage("Sei sicuro di voler uscire dal tuo account?")
        .setPositiveButton("Esci") { dialog, which ->
            viewModel.clearUserData()

            val stopIntent = Intent(requireContext(), PlaybackService::class.java)
            requireContext().stopService(stopIntent)

            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    findNavController().navigate(R.id.action_global_WelcomeFragment)
                }
        }
        .setNegativeButton("Annulla") { dialog, which ->
            dialog.dismiss()
        }
        .show()
}

fun Long.formatAsTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}


fun ImageView.loadCover(url: String?, isHighRes: Boolean = false) {

    val finalUrl = if (isHighRes) {
        url?.replace("100x100bb.jpg", "600x600bb.jpg")
    } else {
        url
    }

    this.load(finalUrl) {
        crossfade(true)
        placeholder(R.drawable.ic_launcher_background)
        error(R.drawable.ic_launcher_foreground)
    }
}



fun Fragment.setupLogoutMenu(viewModel: MusicViewModel) {
    requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.top_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_logout -> {
                    showLogoutDialog(viewModel)
                    true
                }
                else -> false
            }
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}

