package com.example.ballball.`interface`

import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.UsersModel

interface OnItemClickListerner {
    fun onItemClick(requestData: CreateMatchModel)
}