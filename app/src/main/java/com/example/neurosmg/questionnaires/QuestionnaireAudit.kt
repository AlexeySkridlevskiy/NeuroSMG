package com.example.neurosmg.questionnaires

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.neurosmg.databinding.FragmentQuestionnaireAuditBinding

class QuestionnaireAudit : Fragment() {
    lateinit var binding: FragmentQuestionnaireAuditBinding
    private var context: Context? = null
    private var currentQuestion: String = ""
    private val answers: MutableList<String> = mutableListOf()
    private var indexOfQuestion: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionnaireAuditBinding.inflate(inflater)
        val map = readQuestionsFromCSVFile("questions.csv")
        val questionsIterator = map.keys.iterator()
        val tvQuestion = binding.tvQuestion
        val btnAnsw1 = binding.btnAnsw1
        val btnAnsw2 = binding.btnAnsw2
        val btnAnsw3 = binding.btnAnsw3
        val btnAnsw4 = binding.btnAnsw4
        val btnAnsw5 = binding.btnAnsw5

        showNextQuestion(map, questionsIterator)

        btnAnsw1.setOnClickListener { showNextQuestion(map, questionsIterator) }
        btnAnsw2.setOnClickListener { showNextQuestion(map, questionsIterator) }
        btnAnsw3.setOnClickListener { showNextQuestion(map, questionsIterator) }
        btnAnsw4.setOnClickListener { showNextQuestion(map, questionsIterator) }
        btnAnsw5.setOnClickListener { showNextQuestion(map, questionsIterator) }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun showNextQuestion(
        map: Map<String, List<String>>,
        questionsIterator: Iterator<String>
    ) {
        if (questionsIterator.hasNext()) {
            indexOfQuestion++
            currentQuestion = questionsIterator.next()
            answers.clear()
            answers.addAll(map[currentQuestion] ?: emptyList())
        } else {
            // todo: Завершение опросника
        }

        val tvQuestion = binding.tvQuestion
        tvQuestion.text = "$indexOfQuestion. $currentQuestion"

        val btnAnsw1 = binding.btnAnsw1
        val btnAnsw2 = binding.btnAnsw2
        val btnAnsw3 = binding.btnAnsw3
        val btnAnsw4 = binding.btnAnsw4
        val btnAnsw5 = binding.btnAnsw5

        btnAnsw1.visibility = View.VISIBLE
        btnAnsw2.visibility = View.VISIBLE
        btnAnsw3.visibility = View.VISIBLE
        btnAnsw4.visibility = View.VISIBLE
        btnAnsw5.visibility = View.VISIBLE

        if (answers.isNotEmpty()) {
            if(answers.getOrNull(0)!= null){
                btnAnsw1.text = answers[0]
            }else{
                btnAnsw1.visibility = View.GONE
            }

            if(answers.getOrNull(1)!= null){
                btnAnsw2.text = answers[1]
            }else{
                btnAnsw2.visibility = View.GONE
            }

            if(answers.getOrNull(2)!= null){
                btnAnsw3.text = answers[2]
            }else{
                btnAnsw3.visibility = View.GONE
            }

            if(answers.getOrNull(3)!= null){
                btnAnsw4.text = answers[3]
            }else{
                btnAnsw4.visibility = View.GONE
            }

            if(answers.getOrNull(4)!= null){
                btnAnsw5.text = answers[4]
            }else{
                btnAnsw5.visibility = View.GONE
            }
        }
    }

    private fun readQuestionsFromCSVFile(filename: String): Map<String, List<String>> {
        val map = mutableMapOf<String, List<String>>()
        try {
            val inputStream = context?.resources?.assets?.open(filename)
            val reader = inputStream?.bufferedReader()
            var line: String?
            while (reader?.readLine().also { line = it } != null) {
                val parts = line?.split(";")
                if (parts != null && parts.size >= 2) {
                    val question = parts[0]
                    val answers = parts.subList(1, parts.size)
                    map[question] = answers
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MyLog", "$e")
        }
        return map
    }

    companion object {
        @JvmStatic
        fun newInstance() = QuestionnaireAudit()
    }
}