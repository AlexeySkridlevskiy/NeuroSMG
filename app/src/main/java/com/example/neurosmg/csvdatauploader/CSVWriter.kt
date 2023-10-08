package com.example.neurosmg.csvdatauploader

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.reflect.typeOf

class CSVWriter(
    private val context: Context
) {
    val filePath = File(context.getExternalFilesDir(null), "output.csv")

    fun writeDataToCsv(data: List<List<String>>, response: (DataUploadCallback) -> Unit) {
        try {
            val outputStream = FileOutputStream(filePath)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))

            for (row in data) {
                val line = row.joinToString(separator = ",")
                writer.write(line)
                writer.newLine()
            }

            writer.close()
            outputStream.close()

            response(DataUploadCallback.OnSuccess)
        } catch (e: IOException) {
            response(DataUploadCallback.OnFailure)
            e.printStackTrace()
        }

    }
}
