package com.example.ballball.main.match.newcreate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.HighLightOnClickListerner
import com.example.ballball.`interface`.NotHighLightOnClickListerner
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.adapter.NewCreateAdapter
import com.example.ballball.creatematch.CreateMatchActivity
import com.example.ballball.databinding.FragmentNewCreateBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.main.match.newcreate.details.NewCreateDetailsActivity
import com.example.ballball.main.match.upcoming.UpComingViewModel
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCreateFragment : Fragment() {

    private lateinit var newCreateBinding: FragmentNewCreateBinding
    private lateinit var newCreateAdapter : NewCreateAdapter
    private val newCreateViewModel : NewCreateViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initEvents()
        initObserve()
        if (userUID != null) {
            newCreateViewModel.loadNewCreate(userUID)
        }
    }

    private fun initEvents() {
        createMatch()
    }

    private fun createMatch() {
        newCreateBinding.button.setOnClickListener {
            startActivity(Intent(context, CreateMatchActivity::class.java))
            activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
        }
    }

    private fun initObserve() {
        loadNewCreateObserve()
        highlightObserve()
    }

    private fun highlightObserve() {
        newCreateViewModel.highLight.observe(viewLifecycleOwner) {result ->
            when (result) {
                is NewCreateViewModel.HighLightResult.NotHighLightOk -> {}
                is NewCreateViewModel.HighLightResult.NotHighLightError -> {}
                is NewCreateViewModel.HighLightResult.HighLightError -> {}
                is NewCreateViewModel.HighLightResult.HighLightOk -> {}
            }
        }
    }

    private fun loadNewCreateObserve() {
        newCreateViewModel.loadNewCreate.observe(viewLifecycleOwner) {result ->
            with(newCreateBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is NewCreateViewModel.LoadNewCreate.Loading -> {
                    newCreateBinding.progressBar.visibility = View.VISIBLE
                }
                is NewCreateViewModel.LoadNewCreate.ResultOk -> {
                    if (result.list.isEmpty()) {
                        newCreateBinding.imageLayout.visibility = View.VISIBLE
                        newCreateBinding.button.visibility = View.VISIBLE
                        newCreateBinding.recyclerView.visibility = View.GONE
                        newCreateBinding.progressBar.visibility = View.GONE
                    } else {
                        newCreateAdapter.addNewData(result.list)
                    }
                }
                is NewCreateViewModel.LoadNewCreate.ResultError -> {}
            }
        }
    }

    private fun initList() {
        newCreateBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            newCreateAdapter = NewCreateAdapter(arrayListOf())
            adapter = newCreateAdapter

            newCreateAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    NewCreateDetailsActivity.startDetails(context, requestData)
                    activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
                    }
                }
            )

            newCreateAdapter.setOnHighLightClickListerner(object :
                HighLightOnClickListerner {
                override fun onHighLightClickListerner(requestData: CreateMatchModel) {
                    newCreateViewModel.handleHighLight(userUID!!, requestData.matchID)
                }
            })

            newCreateAdapter.setOnNotHighLightClickListerner(object :
                NotHighLightOnClickListerner {
                override fun onNotHighLightClickListerner(requestData: CreateMatchModel) {
                    newCreateViewModel.handleNotHighLight(userUID!!, requestData.matchID)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newCreateBinding = FragmentNewCreateBinding.inflate(layoutInflater)
        return newCreateBinding.root
    }
}