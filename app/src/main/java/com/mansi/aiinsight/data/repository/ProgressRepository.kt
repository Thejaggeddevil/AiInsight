package com.mansi.aiinsight.data.repository

import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProgressRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getApiService(context)

    suspend fun getProgress(): Result<UserProgress> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProgress()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val userProgress = UserProgress(
                        completedLevels = body.completedLevels,
                        currentLevelId = body.currentLevelId,
                        certifications = body.certifications
                    )
                    Result.success(userProgress)
                } else {
                    Result.failure(Exception("Failed to fetch progress"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateProgress(
        currentLevelId: String,
        completedLevels: List<String>
    ): Result<ProgressUpdateResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ProgressUpdateRequest(currentLevelId, completedLevels)
                val response = apiService.updateProgress(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to update progress"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}