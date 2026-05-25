package com.mansi.aiinsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mansi.aiinsight.data.repository.*
import com.mansi.aiinsight.ui.screens.auth.*
import com.mansi.aiinsight.ui.screens.certificate.CertificateScreen
import com.mansi.aiinsight.ui.screens.course.CourseSelectionScreen
import com.mansi.aiinsight.ui.screens.dashboard.DashboardScreen
import com.mansi.aiinsight.ui.screens.learning.LearningModuleScreen
import com.mansi.aiinsight.ui.screens.learning.LessonScreen
import com.mansi.aiinsight.ui.screens.payment.PaymentScreen
import com.mansi.aiinsight.ui.theme.AIInsightTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository(this)
        val courseRepository = CourseRepository(this)
        val certificateRepository = CertificateRepository(this)
        val progressRepository = ProgressRepository(this)
        val paymentRepository = PaymentRepository(this)

        setContent {

            AIInsightTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController =
                        rememberNavController()

                    val startDestination =
                        if (authRepository.isLoggedIn())
                            "dashboard"
                        else
                            "landing"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {

                        // Landing
                        composable("landing") {
                            LandingScreen(navController)
                        }

                        // Register
                        composable("register") {
                            RegisterScreen(
                                navController,
                                authRepository
                            )
                        }

                        // Login
                        composable("login") {
                            LoginScreen(
                                navController,
                                authRepository
                            )
                        }

                        // Course selection
                        composable("courseSelection") {
                            CourseSelectionScreen(
                                navController,
                                courseRepository,
                                authRepository
                            )
                        }

                        // Dashboard
                        composable("dashboard") {
                            DashboardScreen(
                                navController,
                                authRepository,
                                certificateRepository,
                                progressRepository
                            )
                        }

                        // FIXED COURSE LEARNING ROUTE
                        composable(
                            "courseLearning/{level}"
                        ) { backStackEntry ->

                            val level =
                                backStackEntry.arguments
                                    ?.getString("level")
                                    ?: "beginner"

                            LearningModuleScreen(
                                navController =
                                    navController,

                                progressRepository =
                                    progressRepository,

                                level =
                                    level
                            )
                        }

                        // Certificate
                        composable(
                            "certificateDetail/{certId}"
                        ) { backStackEntry ->

                            val certId =
                                backStackEntry.arguments
                                    ?.getString("certId")
                                    ?: ""

                            CertificateScreen(
                                navController,
                                certId,
                                certificateRepository
                            )
                        }

                        // Lesson
                        composable(
                            "lesson/{moduleIndex}"
                        ) { backStackEntry ->

                            val moduleIndex =
                                backStackEntry.arguments
                                    ?.getString("moduleIndex")
                                    ?.toIntOrNull()
                                    ?: 0

                            LessonScreen(
                                navController,
                                moduleIndex
                            )
                        }

                        // Payment
                        composable(
                            "payment/{courseName}"
                        ) { backStackEntry ->

                            val courseName =
                                backStackEntry.arguments
                                    ?.getString("courseName")
                                    ?: ""

                            PaymentScreen(
                                navController,
                                courseName,
                                paymentRepository,
                                authRepository
                            )
                        }

                    }
                }
            }
        }
    }
}