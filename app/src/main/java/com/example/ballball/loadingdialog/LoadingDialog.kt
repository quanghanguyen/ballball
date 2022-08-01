package com.example.ballball.loadingdialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.ballball.R

class LoadingDialog {

    private var dialog : AlertDialog? = null

    fun loadingProgressDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val inflater : LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.progressbar_layout, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog?.show()
    }

    fun dismissProgressDialog() {
        dialog?.dismiss()
    }

}