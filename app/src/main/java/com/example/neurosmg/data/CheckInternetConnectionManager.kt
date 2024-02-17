package com.example.neurosmg.data

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import com.example.neurosmg.R
import com.example.neurosmg.data.local.db.NotSentDataDatabase
import com.example.neurosmg.data.repository.SendFilesDataSource
import com.example.neurosmg.domain.usecase.CheckDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckInternetConnectionManager(private val context: Context) {

    private val database = NotSentDataDatabase.getInstance(context)
    private val notSentDataDao = database.notSavedDataDao()

    private val checkDatabase = CheckDatabase(
        dao = notSentDataDao,
        sendFilesDataSource = SendFilesDataSource(
            context,
            notSentDataDao
        )
    )

    fun checkInternetConnection(): AlertDialog {
        return if (isInternetAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                checkDatabase.invoke()
            }

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

    private fun checkDatabase() {
        val tests = notSentDataDao.getAllTests()
        tests.map {

        }
    }
}