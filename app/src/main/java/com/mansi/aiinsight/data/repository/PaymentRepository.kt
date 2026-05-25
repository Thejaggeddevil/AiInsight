package com.mansi.aiinsight.data.repository

import android.content.Context
import com.mansi.aiinsight.data.api.ApiClient
import com.mansi.aiinsight.data.api.ApiService
import com.mansi.aiinsight.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository(private val context: Context) {

    private val apiService =
        ApiClient.getApiService(context)

    suspend fun createRazorpayOrder(
        amount: Int,
        courseName: String,
        email: String
    ): Result<OrderResponse> {

        return withContext(Dispatchers.IO) {

            try {

                val request =
                    OrderRequest(
                        courseName = courseName,
                        domainId = null
                    )

                val response =
                    apiService.createRazorpayOrder(request)

                if(response.isSuccessful &&
                    response.body()!=null){

                    Result.success(response.body()!!)

                } else {

                    Result.failure(
                        Exception("Failed")
                    )
                }

            } catch(e:Exception){

                Result.failure(e)

            }
        }
    }



    suspend fun verifyPayment(
        razorpayOrderId:String,
        razorpayPaymentId:String,
        razorpaySignature:String,
        courseName:String,
        email:String,
        selectedDomain:String?,
        phone:String?,
        citizen:String?
    ): Result<PaymentVerifyResponse> {

        return withContext(
            Dispatchers.IO
        ){

            try {

                val request =
                    PaymentVerifyRequest(
                        razorpayOrderId,
                        razorpayPaymentId,
                        razorpaySignature,
                        courseName,
                        selectedDomain
                    )

                val response =
                    apiService.verifyPayment(request)

                if(response.isSuccessful &&
                    response.body()!=null){

                    Result.success(
                        response.body()!!
                    )

                } else {

                    Result.failure(
                        Exception(
                            "Verification failed"
                        )
                    )
                }

            } catch(e:Exception){

                Result.failure(e)

            }

        }

    }

}