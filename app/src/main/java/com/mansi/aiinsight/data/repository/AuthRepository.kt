package com.mansi.aiinsight.data.repository

import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getApiService(context)
    private val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    suspend fun sendOtp(fullName: String, email: String, password: String): Result<SendOtpResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = SendOtpRequest(fullName, email, password)
                val response = apiService.sendOtp(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = VerifyOtpRequest(email, otp)
                val response = apiService.verifyOtp(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.token != null) {
                        saveToken(body.token)
                        saveUser(body.user)
                    }
                    Result.success(body)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.loginUser(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    saveToken(body.token)
                    saveUser(body.user)
                    saveLoginData(body)
                    Result.success(body)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun signup(fullName: String, email: String, password: String): Result<SignupResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = SignupRequest(fullName, email, password)
                val response = apiService.signupUser(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun saveToken(token: String) {
        sharedPref.edit().putString("token", token).apply()
    }

    private fun saveUser(user: UserData?) {
        if (user != null) {
            sharedPref.edit().apply {
                putInt("user_id", user.id)
                putString("user_name", user.fullName)
                putString("user_email", user.email)
                putString("user_role", user.role ?: "user")
                apply()
            }
        }
    }

    private fun saveLoginData(response: LoginResponse) {
        sharedPref.edit().apply {
            putBoolean("is_purchased", response.purchased)
            putString("course_name", response.courseName)
            putString("selected_domain", response.selectedDomain)
            putString("payment_verified", response.paymentVerified)
            putString("course_expiry", response.courseExpiry)
            putInt("course_duration", response.duration ?: 0)
            putString("phone", response.phone)
            putString("citizen", response.citizen)
            apply()
        }
    }

    fun getToken(): String? = sharedPref.getString("token", null)

    fun getUser(): UserData? {
        val id = sharedPref.getInt("user_id", -1)
        val name = sharedPref.getString("user_name", null)
        val email = sharedPref.getString("user_email", null)
        val role = sharedPref.getString("user_role", "user")

        return if (id != -1 && email != null && name != null) {
            UserData(id, name, email, role)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun logout() {
        sharedPref.edit().clear().apply()
        ApiClient.clearToken(context)
    }
}