package com.mansi.aiinsight.data.repository

import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.Module
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ModuleRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getApiService(context)

    suspend fun getModules(courseId: Int): Result<List<Module>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getModules(courseId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch modules"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun getModulesByCourse(courseId: Int): Result<List<Module>> {
        return try {
            val response = apiService.getModulesByCourse(courseId)
            Result.success(response.modules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
