package com.mansi.aiinsight.data.api

import com.mansi.aiinsight.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth endpoints
    @POST("send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("user-login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("signup")
    suspend fun signupUser(@Body request: SignupRequest): Response<SignupResponse>

    // Course endpoints
    @GET("api/courses")
    suspend fun getCourses(): Response<CoursesResponse>

    @POST("store-video")
    suspend fun storeVideo(@Body request: VideoRequest): Response<VideoResponse>

    // Payment endpoints
    @POST("test-razorpay")
    suspend fun createRazorpayOrder(@Body request: OrderRequest): Response<OrderResponse>

    @POST("verify-payment")
    suspend fun verifyPayment(@Body request: PaymentVerifyRequest): Response<PaymentVerifyResponse>

    @POST("verify-domain-payment")
    suspend fun verifyDomainPayment(@Body request: PaymentVerifyRequest): Response<PaymentVerifyResponse>

    // Certificate endpoints
    @POST("generate-certificate")
    suspend fun generateCertificate(@Body request: CertificateRequest): Response<CertificateResponse>

    @GET("verify")
    suspend fun verifyCertificate(@Query("certId") certId: String): Response<CertificateVerifyResponse>

    // Progress endpoints — FIXED: GET → POST
    @POST("get-progress")
    suspend fun getProgress(): Response<ProgressResponse>

    @POST("save-progress")
    suspend fun updateProgress(@Body request: ProgressUpdateRequest): Response<ProgressUpdateResponse>

    // Trial + Course status
    @GET("trial-status")
    suspend fun getTrialStatus(): Response<TrialStatusResponse>

    @GET("my-course-status")
    suspend fun getCourseStatus(): Response<CourseStatusResponse>

    // Admin endpoints
    @GET("admin-status")
    suspend fun getAdminStatus(): Response<AdminStatusResponse>

    // OTP endpoints
    @POST("send-mobile-otp")
    suspend fun sendMobileOtp(@Body request: MobileOtpRequest): Response<OtpResponse>

    @POST("verify-mobile-otp")
    suspend fun verifyMobileOtp(@Body request: MobileVerifyRequest): Response<OtpVerifyResponse>
}