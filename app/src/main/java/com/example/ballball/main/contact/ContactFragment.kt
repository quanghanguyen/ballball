package com.example.ballball.main.contact

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnChatClickListerner
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.`interface`.OnNewContactClickListerner
import com.example.ballball.adapter.ContactAdapter
import com.example.ballball.adapter.NewContactAdapter
import com.example.ballball.databinding.AddContactBottomSheetBinding
import com.example.ballball.databinding.FragmentContactBinding
import com.example.ballball.main.chat.ChatFragment
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.model.NewContactModel
import com.example.ballball.model.UsersModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.DatabaseConnection
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ContactFragment : Fragment() {

    private lateinit var contactBinding: FragmentContactBinding
    private lateinit var contactAdapter: ContactAdapter
    private val contactViewModel : ContactViewModel by viewModels()
    private lateinit var newContactAdapter : NewContactAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var addContactBottomSheetBinding: AddContactBottomSheetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initEvents()
        initObserve()
        if (userUID != null) {
            contactViewModel.loadContactList(userUID)
        }
        if (userUID != null) {
            contactViewModel.loadNewContactList(userUID)
        }
    }

    private fun initEvents() {
        addContact()
    }

    private fun addContact() {
        contactBinding.addContact.setOnClickListener {
            val addContactDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
            addContactBottomSheetBinding = AddContactBottomSheetBinding.inflate(layoutInflater)
            addContactDialog.setContentView(addContactBottomSheetBinding.root)

            addContactBottomSheetBinding.save.setOnClickListener {
                val name = addContactBottomSheetBinding.name.text.toString()
                val phoneNumber = addContactBottomSheetBinding.phoneNumber.text.toString()
                if (name.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else {
                    if (userUID != null) {
                        contactViewModel.saveNewContact(userUID, name, phoneNumber)
                        addContactDialog.dismiss()
                    }
                }
            }
            addContactDialog.show()
        }
    }

    private fun initObserve() {
        loadContactObserve()
        addNewContactObserve()
        loadNewContactObserve()
    }

    private fun loadNewContactObserve() {
        contactViewModel.loadNewContactList.observe(viewLifecycleOwner) {result ->
            when (result) {
                is ContactViewModel.LoadNewContactList.ResultOk -> {
                    newContactAdapter.addNewData(result.list)
                    initNewContactSearch()
                }
                is ContactViewModel.LoadNewContactList.ResultError -> {}
            }
        }
    }

    private fun initNewContactSearch() {
        contactBinding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                filterNewContact(s.toString())
            }
        })
    }

    private fun filterNewContact(text: String) {
        if (userUID != null) {
            DatabaseConnection.databaseReference.getReference("newContact").child(userUID).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val filteredList = ArrayList<NewContactModel>()
                        for (requestSnapshot in snapshot.children) {
                            requestSnapshot.getValue(NewContactModel::class.java)?.let {list ->
                                when {
                                    list.name.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) -> {
                                        filteredList.add(0, list)
                                    }
                                }
                            }
                        }
                        newContactAdapter.addFilterList(filteredList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })
        }
    }

    private fun addNewContactObserve() {
        contactViewModel.saveNewContact.observe(viewLifecycleOwner) {result ->
            when (result) {
                is ContactViewModel.SaveNewContact.ResultOk -> {}
                is ContactViewModel.SaveNewContact.ResultError -> {}
            }
        }
    }

    private fun loadContactObserve() {
        contactViewModel.loadContactList.observe(viewLifecycleOwner) {result ->
            with(contactBinding) {
                progressBar.visibility = View.GONE
                mainLayout.visibility = View.VISIBLE
            }
            when (result) {
                is ContactViewModel.LoadContactList.Loading -> {
                    contactBinding.progressBar.visibility = View.VISIBLE
                }
                is ContactViewModel.LoadContactList.ResultOk -> {
                    contactAdapter.addNewData(result.list)
                    initSearch()
                }
                is ContactViewModel.LoadContactList.ResultError -> {}
            }
        }
    }

    private fun initSearch() {
        contactBinding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
    }

    private fun filter(text: String) {
        DatabaseConnection.databaseReference.getReference("Users").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val filteredList = ArrayList<UsersModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(UsersModel::class.java)?.let {list ->
                            when {
                                list.teamName.lowercase(Locale.getDefault()).contains(text.lowercase(
                                    Locale.getDefault()))
                                        && userUID != list.userUid -> {
                                    filteredList.add(0, list)
                                }
                            }
                        }
                    }
                    contactAdapter.addFilterList(filteredList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    private fun initList() {
        contactBinding.recyclerView1.apply {
            layoutManager = LinearLayoutManager(context)
            contactAdapter = ContactAdapter(arrayListOf())
            adapter = contactAdapter

            contactAdapter.setOnIconClickListerner(object :
            OnIconClickListerner{
                override fun onIconClick(requestData: UsersModel) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE),
                            1)
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${requestData.userPhone}"))
                        startActivity(intent)
                        activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
                    }
                }
            })

            contactAdapter.setOnChatClickListerner(object :
            OnChatClickListerner{
                override fun onChatClick(requestData: UsersModel) {
                    ChatDetailsActivity.startDetails(context, requestData)
                    activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
                }
            })
        }

        contactBinding.recyclerView2.apply {
            layoutManager = LinearLayoutManager(context)
            newContactAdapter = NewContactAdapter(arrayListOf())
            adapter = newContactAdapter

            newContactAdapter.setOnNewContactClickListerner(object:
            OnNewContactClickListerner{
                override fun onNewContactClick(list: NewContactModel) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE),
                            1)
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${list.phoneNumber}"))
                        startActivity(intent)
                        activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contactBinding = FragmentContactBinding.inflate(inflater, container, false)
        return contactBinding.root
    }
}