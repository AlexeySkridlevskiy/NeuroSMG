package com.example.neurosmg.data

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import com.example.neurosmg.R

class CheckInternetConnectionManager(private val context: Context) {

    fun checkInternetConnection(): AlertDialog {
        return if (isInternetAvailable()) {
            infoDialog(
                title = context.getString(R.string.internet_connection_title_success),
                subtitle = context.getString(R.string.internet_connection_subtitle_success),
                context = context
            )
        } else {
            infoDialog(
                title = context.getString(R.string.internet_connection_title_error),
                subtitle = context.getString(R.string.internet_connection_subtitle_error),
                context = context
            )
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun infoDialog(
        title: String,
        subtitle: String,
        context: Context
    ): AlertDialog {

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(subtitle)
        alertDialogBuilder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)

        return alertDialog
    }
}