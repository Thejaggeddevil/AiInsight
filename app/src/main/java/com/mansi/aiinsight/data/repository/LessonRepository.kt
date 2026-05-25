package com.mansi.aiinsight.data.repository

import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.LessonDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LessonRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getApiService(context)

    suspend fun getLessonDetails(lessonId: Int): Result<LessonDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLessonDetails(lessonId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch lesson details"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun markLessonComplete(lessonId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.markLessonComplete(lessonId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to mark lesson complete"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
