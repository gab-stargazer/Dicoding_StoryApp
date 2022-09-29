package com.lelestacia.dicodingstoryapp.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.lelestacia.dicodingstoryapp.data.api.DicodingAPI
import com.lelestacia.dicodingstoryapp.data.model.network.AddStoryAndRegisterResponse
import com.lelestacia.dicodingstoryapp.data.model.network.GetStoriesResponse
import com.lelestacia.dicodingstoryapp.data.model.network.LoginResponse
import com.lelestacia.dicodingstoryapp.data.model.network.NetworkStory
import com.lelestacia.dicodingstoryapp.data.network.StoryPagingSource
import com.lelestacia.dicodingstoryapp.utility.NetworkResponse
import com.lelestacia.dicodingstoryapp.utility.Utility
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api: DicodingAPI,
    private val context: Context
) : MainRepository {

    private val isUpdated: MutableLiveData<Boolean> = MutableLiveData(true)

    override suspend fun signUpUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): NetworkResponse<AddStoryAndRegisterResponse> {
        return try {
            NetworkResponse.Success(api.signUpWithEmailAndPassword(name, email, password))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> NetworkResponse.NetworkException
                is HttpException -> {
                    NetworkResponse.GenericException(
                        t.code(),
                        t.message()
                    )
                }
                else -> NetworkResponse.GenericException(null, t.message)
            }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): NetworkResponse<LoginResponse> {
        return try {
            val response = api.signInWithEmailAndPassword(email, password)
            if (!response.error)
                context.getSharedPreferences(Utility.USER_PREF, Context.MODE_PRIVATE)
                    .edit().also {
                        it.putString(Utility.USER_TOKEN, response.loginResult.token)
                        it.putString(Utility.USER_ID, response.loginResult.userId)
                        it.putString(Utility.USERNAME, response.loginResult.name)
                        it.apply()
                    }
            NetworkResponse.Success(response)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> NetworkResponse.NetworkException
                is HttpException -> {
                    NetworkResponse.GenericException(
                        t.code(),
                        t.message()
                    )
                }
                else -> NetworkResponse.GenericException(null, t.message)
            }
        }
    }

    override fun getStoriesWithPagination(): LiveData<PagingData<NetworkStory>> =
        Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(api, getToken())
            }
        ).liveData

    override suspend fun getAllStories(): NetworkResponse<GetStoriesResponse> {
        return try {
            NetworkResponse.Success(api.getStories(getToken()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> NetworkResponse.NetworkException
                is HttpException -> {
                    NetworkResponse.GenericException(
                        t.code(),
                        t.message()
                    )
                }
                else -> NetworkResponse.GenericException(null, t.message)
            }
        }
    }

    override suspend fun getAllStoriesWithLocation(): NetworkResponse<GetStoriesResponse> {
        return try {
            NetworkResponse.Success(api.getAllStoriesWithLocation(getToken()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> NetworkResponse.NetworkException
                is HttpException -> {
                    NetworkResponse.GenericException(
                        t.code(),
                        t.message()
                    )
                }
                else -> NetworkResponse.GenericException(null, t.message)
            }
        }
    }

    override suspend fun uploadStory(
        photo: File,
        description: String
    ): NetworkResponse<AddStoryAndRegisterResponse> {
        return try {
            val descriptionUpload = description.toRequestBody("text/plain".toMediaType())
            val uploadImage: RequestBody = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                name = "photo",
                filename = photo.name,
                body = uploadImage
            )
            isUpdated.postValue(false)
            NetworkResponse.Success(api.addStory(imageMultiPart, descriptionUpload, getToken()))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> NetworkResponse.NetworkException
                is HttpException -> {
                    NetworkResponse.GenericException(
                        t.code(),
                        t.message()
                    )
                }
                else -> NetworkResponse.GenericException(null, t.message)
            }
        }
    }

    override suspend fun uploadStory(
        photo: File,
        description: String,
        lat: Float,
        long: Float
    ): NetworkResponse<AddStoryAndRegisterResponse> {
        return try {
            val descriptionUpload = description.toRequestBody("text/plain".toMediaType())
            val uploadImage: RequestBody = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                photo.name,
                uploadImage
            )
            isUpdated.postValue(false)
            NetworkResponse.Success(api.addStory(imageMultiPart, descriptionUpload, getToken(),lat,long))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> NetworkResponse.NetworkException
                is HttpException -> {
                    NetworkResponse.GenericException(
                        t.code(),
                        t.message()
                    )
                }
                else -> NetworkResponse.GenericException(null, t.message)
            }
        }
    }

    override fun isUpdated(): LiveData<Boolean> = isUpdated

    override fun update() {
        isUpdated.value = true
    }

    private fun getToken(): String =
        "Bearer ${
            context
                .getSharedPreferences(Utility.USER_PREF, Context.MODE_PRIVATE)
                .getString(Utility.USER_TOKEN, "")}"

}