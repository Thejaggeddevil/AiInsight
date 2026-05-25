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
import com.mansi.aiinsight.data.model.UserProgress
import com.mansi.aiinsight.data.repository.*
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    navController: NavController,
    authRepository: AuthRepository,
    certificateRepository: CertificateRepository,
    progressRepository: ProgressRepository
) {
    var progressData by remember { mutableStateOf<UserProgress?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val user = authRepository.getUser()

    LaunchedEffect(Unit) {
        scope.launch {
            val result = progressRepository.getProgress()
            result.onSuccess {
                progressData = it
                isLoading = false
            }
            result.onFailure {
                errorMessage = it.message ?: "Failed to load progress"
                isLoading = false
            }
        }
    }

    val completedLevels = progressData?.completedLevels ?: emptyList()
    val certs = progressData?.certifications ?: emptyList()
    val isBeginnerDone = completedLevels.contains("beginner")

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
                        text = user?.fullName ?: "Learner",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
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
                text = user?.fullName ?: "Learner",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── LOADING / ERROR ──
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(Color(0xFF1A0000), RoundedCornerShape(12.dp))
                    .border(0.5.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp
                )
            }
        }

        // ── COURSE LEVELS ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Box(
                modifier = Modifier
                    .background(Color(0xFF121212), RoundedCornerShape(16.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "YOUR COURSES",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Beginner
            CourseCard(
                number = "01",
                title = "Beginner",
                description = "Learn the foundations of prompt engineering. Master basic techniques to communicate effectively with AI.",
                tag = "FREE",
                tagColor = Color(0xFF166534),
                tagTextColor = Color(0xFF4ADE80),
                isCompleted = isBeginnerDone,
                isUnlocked = true,
                onStart = { navController.navigate("courseLearning/beginner") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Intermediate
            CourseCard(
                number = "02",
                title = "Intermediate",
                description = "Master domain-specific prompting for Business, Marketing, Healthcare, Education and more.",
                tag = "PAID",
                tagColor = Color(0xFF1E3A5F),
                tagTextColor = Color(0xFF60A5FA),
                isCompleted = false,
                isUnlocked = isBeginnerDone,
                onStart = { navController.navigate("courseLearning/intermediate") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Advanced
            CourseCard(
                number = "03",
                title = "Advanced",
                description = "Expert level prompt engineering techniques for professionals and power users.",
                tag = "PAID",
                tagColor = Color(0xFF1E3A5F),
                tagTextColor = Color(0xFF60A5FA),
                isCompleted = false,
                isUnlocked = false,
                onStart = { navController.navigate("courseLearning/advanced") }
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── CERTIFICATIONS ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Box(
                modifier = Modifier
                    .background(Color(0xFF121212), RoundedCornerShape(16.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "COMPLETED CERTIFICATIONS (${certs.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (certs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0A0A), RoundedCornerShape(20.dp))
                        .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Certificates Yet",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complete levels to earn verifiable certificates",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { navController.navigate("courseLearning/beginner") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Start Beginner Course →",
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    certs.forEach { cert ->
                        CertCard(
                            cert = cert,
                            onClick = { navController.navigate("certificateDetail/${cert.id}") }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // ── FOOTER ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.White.copy(alpha = 0.08f))
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "© 2024 AIINSIGHT • Secure Learning Portal",
                color = Color(0xFF444444),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Identity Verification Powered by DigiLocker Framework",
                color = Color(0xFF333333),
                fontSize = 10.sp
            )
        }
    }
}

// ── COURSE CARD ──
@Composable
private fun CourseCard(
    number: String,
    title: String,
    description: String,
    tag: String,
    tagColor: Color,
    tagTextColor: Color,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A), RoundedCornerShape(20.dp))
            .border(
                0.5.dp,
                if (isCompleted) Color(0xFF166534).copy(alpha = 0.5f)
                else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                if (isCompleted) Color(0xFF166534).copy(alpha = 0.3f)
                                else if (isUnlocked) Color.White.copy(alpha = 0.08f)
                                else Color.White.copy(alpha = 0.03f),
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
                                .background(tagColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = tagTextColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                if (!isUnlocked && !isCompleted) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = description,
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

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

// ── CERT CARD ──
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