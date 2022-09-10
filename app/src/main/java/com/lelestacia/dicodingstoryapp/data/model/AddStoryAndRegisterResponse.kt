package com.lelestacia.dicodingstoryapp.data.model

import com.google.gson.annotations.SerializedName

data class AddStoryAndRegisterResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
