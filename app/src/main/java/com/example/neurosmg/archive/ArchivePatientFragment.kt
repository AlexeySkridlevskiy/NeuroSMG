package com.example.neurosmg.archive

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.common.State
import com.example.neurosmg.databinding.FragmentArchivePatientBinding
import com.example.neurosmg.patientTestList.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val PATIENT_ID = "patient_id"

class ArchivePatientFragment : Fragment() {

    private lateinit var binding: FragmentArchivePatientBinding

    private var patientId: Int = -1
    private val adapter = RecyclerAdapter<String>()

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[ArchivePatientViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patientId = it.getInt(PATIENT_ID)
        }
        viewModel.initPatientArchive(patientId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchivePatientBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.archive.collect { state ->
                when (state) {
                    is ArchiveViewState.EmptyDownloadedFile -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            "Ошибка: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ArchiveViewState.ErrorDownloadFile -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            "Ошибка: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ArchiveViewState.ErrorGetListFiles -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            "Простите, попробуйте позже",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    ArchiveViewState.ListFromServerIsEmpty -> {
                        binding.progressBar.isVisible = false
                        binding.tvEmptyListWarning.isVisible = true
                    }

                    ArchiveViewState.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is ArchiveViewState.SuccessDownloadFile -> {
                        binding.progressBar.isVisible = false
                        val shareIntent = createShareFileIntent(
                            state.file,
                            "com.example.neurosmg.provider",
                            state.fileName
                        )
                        requireActivity().startActivity(shareIntent)
                    }

                    is ArchiveViewState.SuccessGetListFiles -> {
                        binding.progressBar.isVisible = false
                        adapter.addItem(state.listOfArchive)
                        binding.recyclerView.adapter = adapter
                    }
                }
            }
        }

        adapter.onItemClick = object : RecyclerAdapter.OnItemClickListener<String> {
            override fun onItemClick(item: String) {
                viewModel.downloadSelectedFile(fileName = item)
            }
        }
    }

    private fun createShareFileIntent(file: File, fileType: String, chooserTitle: String): Intent {
        val contentUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.neurosmg.provider", //если будет не работать шеринг - поменять тут название файла
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/*"
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // Добавьте следующую строку, чтобы указать действие отправки файла
        intent.action = Intent.ACTION_SEND

        return Intent.createChooser(intent, chooserTitle)
    }

    companion object {
        @JvmStatic
        fun newInstance(patientId: Int) =
            ArchivePatientFragment().apply {
                arguments = Bundle().apply {
                    putInt(PATIENT_ID, patientId)
                }
            }
    }
}