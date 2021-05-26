package com.tsu.alotofquestions.data

import com.tsu.alotofquestions.data.model.LoggedInUser
import com.tsu.alotofquestions.data.model.Token
import com.tsu.alotofquestions.data.model.UserAuth
import com.tsu.alotofquestions.data.network.APIFactory
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val remote = APIFactory.APIService

    suspend fun login(username: String, password: String, deviceId: String): Result<Token> {

        try {
            val response = remote.loginAsync(UserAuth(deviceId, username, password)).await()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Result.Success(body)
            }
            return Result.Error(Exception(response.message()))  //error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return Result.Error(e)//error(e.message ?: e.toString())
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}