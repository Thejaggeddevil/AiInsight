package com.mansi.aiinsight.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mansi.aiinsight.data.model.Certification
import com.mansi.aiinsight.data.model.Course
import com.mansi.aiinsight.data.repository.*
import kotlinx.coroutines.launch

// Data class for dashboard aggregation
data class DashboardData(
    val user: DashboardUser,
    val courses: List<Course>,
    val progress: UserProgressData
)

data class DashboardUser(
    val fullName: String,
    val email: String
)

data class UserProgressData(
    val completedLessonsCount: Int,
    val certifications: List<Certification>
)

@Composable
fun DashboardScreen(
    navController: NavController,
    authRepository: AuthRepository,
    certificateRepository: CertificateRepository,
    progressRepository: ProgressRepository
) {
    var dashboardData by remember { mutableStateOf<DashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val user = authRepository.getUser()

    // Function to refresh dashboard
    fun refreshDashboard() {
        isLoading = true
        errorMessage = ""
        scope.launch {
            try {
                val progressResult = progressRepository.getProgress()
                progressResult.onSuccess { progress ->
                    try {
                        // Safe navigation with null checks
                        val completedLevels = progress?.completedLevels ?: emptyList()
                        val certs = progress?.certifications ?: emptyList()
                        val userName = user?.fullName ?: "Learner"
                        val userEmail = user?.email ?: ""

                        val dashData = DashboardData(
                            user = DashboardUser(
                                fullName = userName,
                                email = userEmail
                            ),
                            courses = emptyList(),
                            progress = UserProgressData(
                                completedLessonsCount = completedLevels.size,
                                certifications = certs
                            )
                        )
                        dashboardData = dashData
                        isLoading = false
                        errorMessage = ""
                    } catch (e: Exception) {
                        // Fallback with empty data
                        val dashData = DashboardData(
                            user = DashboardUser(
                                fullName = user?.fullName ?: "Learner",
                                email = user?.email ?: ""
                            ),
                            courses = emptyList(),
                            progress = UserProgressData(
                                completedLessonsCount = 0,
                                certifications = emptyList()
                            )
                        )
                        dashboardData = dashData
                        isLoading = false
                    }
                }
                progressResult.onFailure { exception ->
                    // Fallback data on error
                    val dashData = DashboardData(
                        user = DashboardUser(
                            fullName = user?.fullName ?: "Learner",
                            email = user?.email ?: ""
                        ),
                        courses = emptyList(),
                        progress = UserProgressData(
                            completedLessonsCount = 0,
                            certifications = emptyList()
                        )
                    )
                    dashboardData = dashData
                    errorMessage = exception.message ?: "Failed to load progress"
                    isLoading = false
                }
            } catch (e: Exception) {
                // Fallback data on exception
                val dashData = DashboardData(
                    user = DashboardUser(
                        fullName = user?.fullName ?: "Learner",
                        email = user?.email ?: ""
                    ),
                    courses = emptyList(),
                    progress = UserProgressData(
                        completedLessonsCount = 0,
                        certifications = emptyList()
                    )
                )
                dashboardData = dashData
                errorMessage = e.message ?: "Failed to load dashboard"
                isLoading = false
            }
        }
    }

    // Initial load
    LaunchedEffect(refreshTrigger) {
        refreshDashboard()
    }

    val certifications = dashboardData?.progress?.certifications ?: emptyList()
    val completedLessonsCount = dashboardData?.progress?.completedLessonsCount ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {

        // ── NAVBAR ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0A0A))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AIINSIGHT",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 3.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "VERIFIED USER",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = dashboardData?.user?.fullName ?: user?.fullName ?: "Learner",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = {
                        authRepository.logout()
                        navController.navigate("landing") {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Color(0xFF7F1D1D).copy(alpha = 0.8f),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── WELCOME ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Welcome back,",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = dashboardData?.user?.fullName ?: user?.fullName ?: "Learner",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── CONTENT SECTION ──
        if (isLoading) {
            // LOADING STATE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            // ── ERROR MESSAGE (if any) ──
            if (errorMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .background(Color(0xFF1A0000), RoundedCornerShape(12.dp))
                        .border(0.5.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { refreshTrigger++ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // ── STATS ROW ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    number = certifications.size.toString(),
                    label = "Certificates",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = completedLessonsCount.toString(),
                    label = "Lessons Complete",
                    modifier = Modifier.weight(1f)
                )
            }

            // ── CERTIFICATES SECTION ──
            if (certifications.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Your Certificates",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        certifications.forEach { cert ->
                            CertCard(
                                cert = cert,
                                onClick = {
                                    if (cert.certId != null) {
                                        navController.navigate("certificateDetail/${cert.certId}")
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            } else {
                // Empty certificates state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No certificates yet. Start learning!",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            // ── COURSES SECTION ──
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Continue Learning",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Sample course cards
                repeat(3) { index ->
                    CourseCard(
                        number = "${index + 1}",
                        title = "Beginner Level",
                        tag = "NEW",
                        description = "Master the fundamentals and build a strong foundation",
                        isCompleted = false,
                        isUnlocked = index == 0,
                        onStart = {
                            navController.navigate("courseLearning/beginner/${index + 1}")
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a1a)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = number,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    number: String,
    title: String,
    tag: String,
    description: String,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a1a)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with number and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            if (isCompleted) Color(0xFF166534).copy(alpha = 0.2f)
                            else if (isUnlocked)
                                Color.White.copy(alpha = 0.03f)
                            else
                                Color.White.copy(alpha = 0.03f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            0.5.dp,
                            if (isCompleted) Color(0xFF4ADE80).copy(alpha = 0.3f)
                            else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Text(
                            text = number,
                            color = if (isUnlocked) Color.White else Color.Gray,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        color = if (isUnlocked || isCompleted) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF7F6B2F), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            color = Color(0xFFFFD700),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Description
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            // Footer with action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when {
                    isCompleted -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF166534).copy(alpha = 0.2f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    0.5.dp,
                                    Color(0xFF4ADE80).copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "✓ Completed",
                                color = Color(0xFF4ADE80),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    isUnlocked -> {
                        Button(
                            onClick = onStart,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = "Start Course →",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    0.5.dp,
                                    Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "🔒 Complete previous level",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CertCard(
    cert: Certification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    0.5.dp,
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cert.levelName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color(0xFF166534).copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}