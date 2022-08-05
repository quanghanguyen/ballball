package com.example.ballball.main.home.all

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.databinding.FragmentAllBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllFragment : Fragment() {

    private lateinit var allFragmentBinding : FragmentAllBinding
    private val allViewModel : AllViewModel by viewModels()
    private lateinit var allAdapter : HomeAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if (userUID != null) {
            allViewModel.loadAll(userUID)
        }
    }

    private fun initObserve() {
        allViewModel.loadAllList.observe(viewLifecycleOwner) {result ->
            when (result) {
                is AllViewModel.LoadAllList.ResultOk -> {
                    allAdapter.addNewData(result.list)
                }
                is AllViewModel.LoadAllList.ResultError -> {
                    Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initList() {
        allFragmentBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            allAdapter = HomeAdapter(arrayListOf())
            adapter = allAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        allFragmentBinding = FragmentAllBinding.inflate(inflater, container, false)
        return allFragmentBinding.root
    }
}