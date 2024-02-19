package com.example.neurosmg.utils

import SoundPlayer
import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

fun showInfoDialog(
    title: String,
    message: String,
    soundResource: Int? = null,
    buttonText: String,
    context: Context,
    soundPlayer: SoundPlayer? = null,
    positiveClickListener: () -> Unit
) {
    val alertDialogBuilder = AlertDialog.Builder(context)
    soundResource?.let {
        soundPlayer?.playSound(soundResource)
    }
    alertDialogBuilder.setTitle(title)
    alertDialogBuilder.setMessage(message)
    alertDialogBuilder.setPositiveButton(buttonText) { dialog, _ ->
        soundPlayer?.stopSound()
        dialog.dismiss()
        positiveClickListener()
    }

    val alertDialog: AlertDialog = alertDialogBuilder.create()
    alertDialog.show()
    alertDialog.setCanceledOnTouchOutside(false)
}

fun showToast(
    context: Context,
    message: String
) {
    val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
    toast.show()
}