package com.tsu.alotofquestions.data.network

import com.tsu.alotofquestions.data.model.Task
import com.tsu.alotofquestions.data.model.Token
import com.tsu.alotofquestions.data.model.UserAuth
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface SecretService {
    @Headers("Content-Type: application/json")
    @POST("users/auth")
    fun loginAsync(@Body user: UserAuth): Deferred<Response<Token>>

    @GET("task")
    fun getTaskAsync(@Header("Authorization") bearerToken: String): Deferred<Response<Task>>
}