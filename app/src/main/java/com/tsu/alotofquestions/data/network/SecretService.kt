package com.tsu.alotofquestions.data.network

import com.tsu.alotofquestions.data.model.*
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface SecretService {
    @Headers("Content-Type: application/json")
    @POST("users/auth")
    fun loginAsync(@Body user: UserAuth): Deferred<Response<Token>>

    @GET("task")
    fun getTaskAsync(@Header("Authorization") bearerToken: String): Deferred<Response<Task>>

    @Headers("Content-Type: application/json")
    @POST("task/check")
    fun sendAnswerAsync(@Header("Authorization") bearerToken: String,
                        @Body answer: Answer
    ): Deferred<Response<TaskCheckResult>>

}