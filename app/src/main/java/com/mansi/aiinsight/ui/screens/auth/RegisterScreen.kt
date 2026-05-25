package com.mansi.aiinsight.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mansi.aiinsight.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, authRepository: AuthRepository) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showOtpForm by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(120) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(showOtpForm) {
        if (showOtpForm) {
            var remainingTime = 120
            while (remainingTime > 0) {
                kotlinx.coroutines.delay(1000)
                remainingTime--
                timer = remainingTime
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showOtpForm) "Verify OTP" else "Create Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!showOtpForm) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            val result = authRepository.sendOtp(fullName, email, password)
                            result.onSuccess {
                                showOtpForm = true
                                timer = 120
                                errorMessage = ""
                            }
                            result.onFailure {
                                errorMessage = it.message ?: "Failed to send OTP"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLoading) "Sending OTP..." else "Register",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Time Remaining: ${timer / 60}:${String.format("%02d", timer % 60)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            val result = authRepository.verifyOtp(email, otp)
                            result.onSuccess {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                            result.onFailure {
                                errorMessage = it.message ?: "Invalid OTP"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    enabled = !isLoading && timer > 0
                ) {
                    Text(
                        if (isLoading) "Verifying..." else "Verify & Register",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (timer == 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                val result = authRepository.sendOtp(fullName, email, password)
                                result.onSuccess {
                                    timer = 120
                                    errorMessage = ""
                                }
                                result.onFailure {
                                    errorMessage = it.message ?: "Failed to resend OTP"
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Resend OTP")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = if (showOtpForm) "Didn't get OTP?" else "Already have an account? ",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Login", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}