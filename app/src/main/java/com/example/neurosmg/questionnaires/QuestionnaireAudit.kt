package com.example.neurosmg.questionnaires

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.neurosmg.databinding.FragmentQuestionnaireAuditBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class QuestionnaireAudit : Fragment() {
    lateinit var binding: FragmentQuestionnaireAuditBinding

    val map = readQuestionsFromCSVFile("questions.csv")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionnaireAuditBinding.inflate(inflater)

        val firstQuestion = map.entries.first()
        val questionText = firstQuestion.key
        val answers = firstQuestion.value

        Log.d("MyLog", questionText)
        answers.forEachIndexed { index, answer ->
            Log.d("MyLog", "${index + 1}. $answer")
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = QuestionnaireAudit()
    }

    private fun readQuestionsFromCSVFile(filename: String): Map<String, List<String>> {
        val map = mutableMapOf<String, List<String>>()
        File(filename).useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(";")
                if (parts.size >= 2) {
                    val question = parts[0]
                    val answers = parts.subList(1, parts.size)
                    map[question] = answers
                }
            }
        }
        return map
    }
}
