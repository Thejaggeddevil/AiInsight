package com.mansi.aiinsight.data.model

import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey

// Auth Requests
data class SendOtpRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Auth Responses
data class SendOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String? = null
)

data class VerifyOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserData? = null
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserData,
    @SerializedName("purchased") val purchased: Boolean,
    @SerializedName("progress") val progress: UserProgress,
    @SerializedName("payment_verified") val paymentVerified: String,
    @SerializedName("courseName") val courseName: String? = null,
    @SerializedName("selectedDomain") val selectedDomain: String? = null,
    @SerializedName("courseexpairy") val courseExpiry: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("citizen") val citizen: String? = null
)

data class SignupResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: UserData? = null
)

// FIXED: id is Int not String (backend sends "id":11)
data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String? = "user"
)

// Course Requests & Responses
data class CoursesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("courses") val courses: List<Course>
)

data class Course(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("duration") val duration: Int
)

data class VideoRequest(
    @SerializedName("title") val title: String,
    @SerializedName("video") val video: String
)

data class VideoResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

// Payment Requests & Responses
data class OrderRequest(
    @SerializedName("courseName") val courseName: String,
    @SerializedName("domainId") val domainId: String? = null
)

data class OrderResponse(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("currency") val currency: String,
    @SerializedName("key") val key: String
)

data class PaymentVerifyRequest(
    @SerializedName("razorpay_order_id") val razorpayOrderId: String,
    @SerializedName("razorpay_payment_id") val razorpayPaymentId: String,
    @SerializedName("razorpay_signature") val razorpaySignature: String,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("selectedDomain") val selectedDomain: String? = null
)

data class PaymentVerifyResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)

// Certificate Requests & Responses
data class CertificateRequest(
    @SerializedName("certificateId") val certificateId: String,
    @SerializedName("name") val name: String,
    @SerializedName("course") val course: String
)

data class CertificateResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class CertificateVerifyResponse(
    @SerializedName("status") val status: String,
    @SerializedName("certificate") val certificate: CertificateData? = null,
    @SerializedName("message") val message: String? = null
)

data class CertificateData(
    @SerializedName("certificateId") val certificateId: String,
    @SerializedName("learnerName") val learnerName: String,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("issueDate") val issueDate: String
)

// Progress Requests & Responses
data class ProgressResponse(
    @SerializedName("completedLevels") val completedLevels: List<String>,
    @SerializedName("currentLevelId") val currentLevelId: String,
    @SerializedName("certifications") val certifications: List<Certification>
)

data class UserProgress(
    @SerializedName("completedLevels") val completedLevels: List<String>,
    @SerializedName("currentLevelId") val currentLevelId: String,
    @SerializedName("certifications") val certifications: List<Certification>
)

data class Certification(
    @SerializedName("id") val id: String,
    @SerializedName("learnerName") val learnerName: String? = null,
    @SerializedName("levelName") val levelName: String,
    @SerializedName("date") val date: String? = null,
    @SerializedName("certId") val certId: String? = null,
    @SerializedName("course") val course: String? = null
)

data class ProgressUpdateRequest(
    @SerializedName("currentLevelId") val currentLevelId: String,
    @SerializedName("completedLevels") val completedLevels: List<String>
)

data class ProgressUpdateResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

// Trial Status Response
data class TrialStatusResponse(
    @SerializedName("trialActive") val trialActive: Boolean,
    @SerializedName("trialExpired") val trialExpired: Boolean,
    @SerializedName("trialDaysRemaining") val trialDaysRemaining: Int,
    @SerializedName("hasPaidCourse") val hasPaidCourse: Boolean,
    @SerializedName("trialEnd") val trialEnd: String? = null
)

// Course Status Response
data class CourseStatusResponse(
    @SerializedName("courseexpairy") val courseExpiry: String? = null,
    @SerializedName("trialActive") val trialActive: Boolean? = null,
    @SerializedName("trialEnd") val trialEnd: String? = null
)

// Admin Response
data class AdminStatusResponse(
    @SerializedName("isAdmin") val isAdmin: Boolean
)

// Mobile OTP Requests
data class MobileOtpRequest(
    @SerializedName("mobile") val mobile: String
)

data class MobileVerifyRequest(
    @SerializedName("mobile") val mobile: String,
    @SerializedName("otp") val otp: String
)

data class OtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class OtpVerifyResponse(
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("message") val message: String
)

// Room Database Entities
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val id: String,
    val token: String,
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val name: String,
    val amount: Int,
    val duration: Int
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val id: String,
    val learnerName: String,
    val courseName: String,
    val issueDate: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)