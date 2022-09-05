package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.ballball.R
import javax.inject.Inject

class SpinnerAdapter @Inject constructor(
    private val locationList : Array<String>,
    private val locationAddressList : Array<String>,
    private val context: android.content.Context
    ) : BaseAdapter() {

    override fun getCount(): Int {
        return locationList.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_custom_layout, null)
        val locationName = view.findViewById<TextView>(R.id.location_name)
        val locationAddress = view.findViewById<TextView>(R.id.spinner_location_address)
        locationName.text = locationList[p0]
        locationAddress.text = locationAddressList[p0]
        return view
    }
}