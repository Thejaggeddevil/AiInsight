package com.mansi.aiinsight.data.repository



import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CertificateRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getApiService(context)

    suspend fun generateCertificate(
        certificateId: String,
        name: String,
        course: String
    ): Result<CertificateResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CertificateRequest(certificateId, name, course)
                val response = apiService.generateCertificate(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to generate certificate"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun verifyCertificate(certId: String): Result<CertificateData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verifyCertificate(certId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "valid" && body.certificate != null) {
                        Result.success(body.certificate!!)
                    } else {
                        Result.failure(Exception("Certificate not found"))
                    }
                } else {
                    Result.failure(Exception("Verification failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun generateCertificateId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (0..9999).random()
        return "CERT-$timestamp-$random"
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}