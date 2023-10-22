package com.example.neurosmg.csvdatauploader

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

class CSVWriter(
    private val context: Context
) {

    fun writeDataToCsv(
        data: List<List<String>>,
        fileName: String,
        response: (DataUploadCallback) -> Unit
    ) {
        val filePath = File(context.getExternalFilesDir(null), fileName)

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
