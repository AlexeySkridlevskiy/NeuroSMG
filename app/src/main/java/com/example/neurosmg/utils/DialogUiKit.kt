package com.example.neurosmg.utils

import SoundPlayer
import android.app.AlertDialog
import android.content.Context

fun showInfoDialog(
    title: String,
    message: String,
    soundResource: Int,
    buttonText: String,
    context: Context,
    soundPlayer: SoundPlayer?,
    positiveClickListener: () -> Unit
) {
    val alertDialogBuilder = AlertDialog.Builder(context)
    soundPlayer?.playSound(soundResource)
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