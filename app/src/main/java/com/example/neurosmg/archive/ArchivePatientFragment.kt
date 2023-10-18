package com.example.neurosmg.archive

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.neurosmg.common.State
import com.example.neurosmg.databinding.FragmentArchivePatientBinding
import com.example.neurosmg.patientTestList.RecyclerAdapter

private const val PATIENT_ID = "patient_id"

class ArchivePatientFragment : Fragment() {

    private lateinit var binding: FragmentArchivePatientBinding

    private var patientId: Int = -1
    private val adapter = RecyclerAdapter<Int>()

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

        viewModel.archive.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.Empty -> {
                    binding.progressBar.isVisible = false
                }

                is State.Error -> {
                    binding.progressBar.isVisible = false
                }

                State.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is State.Success -> {
                    binding.progressBar.isVisible = false
                    adapter.addItem(state.data.listOfArchive)
                    binding.recyclerView.adapter = adapter
                }
            }
        }
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