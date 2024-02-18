package com.example.neurosmg.questionnaires

import SoundPlayer
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.neurosmg.KeyOfArgument
import com.example.neurosmg.R
import com.example.neurosmg.csvdatauploader.CSVWriter
import com.example.neurosmg.csvdatauploader.DataUploadCallback
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.databinding.FragmentQuestionnaireAuditBinding
import com.example.neurosmg.tests.cbt.CbtTestViewModel

class QuestionnaireAudit : Fragment() {
    lateinit var binding: FragmentQuestionnaireAuditBinding
    private var context: Context? = null
    private var currentQuestion: String = ""
    private val answers: MutableList<String> = mutableListOf()
    private var indexOfQuestion: Int = 0

    private var soundPlayer: SoundPlayer? = null

    private val viewModelUploaderFile by lazy {
        ViewModelProvider(requireActivity())[CbtTestViewModel::class.java]
    }
    private val data = mutableListOf<MutableList<String>>()
    private var patientId: Int = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelUploaderFile.setInitialState()
        patientId = arguments?.getInt(KeyOfArgument.KEY_OF_ID_PATIENT) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionnaireAuditBinding.inflate(inflater)
        val map = readQuestionsFromCSVFile("audit.csv")
        val questionsIterator = map.keys.iterator()
        val tvQuestion = binding.tvQuestion
        val btnAnsw1 = binding.btnAnsw1
        val btnAnsw2 = binding.btnAnsw2
        val btnAnsw3 = binding.btnAnsw3
        val btnAnsw4 = binding.btnAnsw4
        val btnAnsw5 = binding.btnAnsw5

        showNextQuestion(map, questionsIterator)

        btnAnsw1.setOnClickListener {
            saveData(1)
            showNextQuestion(map, questionsIterator)
        }
        btnAnsw2.setOnClickListener {
            saveData(2)
            showNextQuestion(map, questionsIterator)
        }
        btnAnsw3.setOnClickListener {
            saveData(3)
            showNextQuestion(map, questionsIterator)
        }
        btnAnsw4.setOnClickListener {
            saveData(4)
            showNextQuestion(map, questionsIterator)
        }
        btnAnsw5.setOnClickListener {
            saveData(5)
            showNextQuestion(map, questionsIterator)
        }

        viewModelUploaderFile.uploadFileLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UploadState.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                UploadState.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is UploadState.Success.SuccessGetIdFile -> {
                    binding.progressBar.isVisible = true
                    Toast.makeText(requireContext(), "$state", Toast.LENGTH_SHORT).show()
                }

                UploadState.Success.SuccessSendFile -> {
                    binding.progressBar.isVisible = false
                    parentFragmentManager.popBackStack()
                }

                UploadState.Initial -> {}
            }
        }
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
            finishTest()
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

    private fun saveData(i: Int) {
        val unixTimestamp = System.currentTimeMillis()
        val dynamicRow = mutableListOf(
            indexOfQuestion.toString(), unixTimestamp.toString(),
            i.toString()
        )
        data.add(dynamicRow)
    }

    private fun finishTest() {
        binding.constraintMain.visibility = View.INVISIBLE

        saveDataToFileCSV()
    }

    private fun saveDataToFileCSV() {
        val csvWriter = CSVWriter(context = requireContext())
        val unixTime = System.currentTimeMillis()
        val fileName = "${TEST_NAME}.${unixTime}${TEST_FILE_EXTENSION}" //поменять файл на нужный
        csvWriter.writeDataToCsv(data, fileName = fileName) {
            when (it) {
                DataUploadCallback.OnFailure -> {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.not_success_save_file),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                DataUploadCallback.OnSuccess -> {
                    infoDialogEndTest(fileName)
                }
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

    private fun infoDialogEndTest(fileName: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        soundPlayer?.playSound(R.raw.finish)
        alertDialogBuilder.setTitle("Тестирование пройдено!") // TODO: в ресурсы выноси
        alertDialogBuilder.setMessage("Данные будут сохранены в папку") // TODO: в ресурсы выноси
        alertDialogBuilder.setPositiveButton("Окей") { dialog, _ -> // TODO: в ресурсы выноси
            viewModelUploaderFile.sendFile(
                idPatient = patientId,
                fileName = fileName,
                data = data
            )
            soundPlayer?.stopSound()
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val TEST_NAME = "AUDIT"
        private const val TEST_FILE_EXTENSION = ".csv"
        @JvmStatic
        fun newInstance(
            patientId: Int = -1
        ): QuestionnaireAudit {
            val fragment = QuestionnaireAudit()
            val args = Bundle()
            args.putInt(KeyOfArgument.KEY_OF_ID_PATIENT, patientId)
            fragment.arguments = args
            return fragment
        }
    }
}