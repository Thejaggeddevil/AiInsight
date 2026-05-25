package com.mansi.aiinsight.data.repository



import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getApiService(context)
    private val sharedPref = context.getSharedPreferences("courses", Context.MODE_PRIVATE)

    suspend fun getCourses(): Result<List<Course>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCourses()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.courses)
                } else {
                    Result.failure(Exception("Failed to fetch courses"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun saveSelectedCourse(courseName: String) {
        sharedPref.edit().putString("selected_course", courseName).apply()
    }

    fun getSelectedCourse(): String? = sharedPref.getString("selected_course", null)
}