package com.example.neurosmg.csvdatauploader

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CSVWriter(private val fileName: String) {
    fun writeDataToCsv(data: List<List<String>>) {
        try {
            val file = File(fileName)
            val csvWriter = FileWriter(file)

            for (row in data) {
                csvWriter.append(row.joinToString(","))
                csvWriter.append("\n")
            }

            csvWriter.flush()
            csvWriter.close()

            Log.d("MyLog", "Данные успешно записаны в $fileName")
        } catch (e: IOException) {
           Log.d("MyLog","Ошибка при записи данных в $fileName: ${e.message}")
        }
    }
}
