package com.mansi.aiinsight.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mansi.aiinsight.data.model.LessonDetails
import com.mansi.aiinsight.data.repository.LessonRepository
import com.mansi.aiinsight.data.repository.ProgressRepository
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: Int,
    lessonRepository: LessonRepository,
    progressRepository: ProgressRepository
) {
    var lessonDetails by remember { mutableStateOf<LessonDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Function to load lesson details
    fun loadLessonDetails() {
        isLoading = true
        scope.launch {
            val result = lessonRepository.getLessonDetails(lessonId)
            result.onSuccess {
                lessonDetails = it
                isCompleted = it.isCompleted
                isLoading = false
                errorMessage = ""
            }
            result.onFailure {
                errorMessage = it.message ?: "Failed to load lesson"
                isLoading = false
            }
        }
    }

    // Initial load
    LaunchedEffect(refreshTrigger) {
        loadLessonDetails()
    }

    // Function to mark lesson as complete
    fun markLessonComplete() {
        isSubmitting = true
        scope.launch {
            val result = lessonRepository.markLessonComplete(lessonId)
            result.onSuccess {
                isCompleted = true
                isSubmitting = false
                // Refresh progress in background (optional)
                progressRepository.getProgress()
            }
            result.onFailure {
                errorMessage = it.message ?: "Failed to mark lesson complete"
                isSubmitting = false
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ── HEADER WITH BACK BUTTON ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLoading) "Loading..." else "Lesson Details",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = lessonDetails?.name ?: "Lesson",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                }
            }

            if (isLoading) {
                // ── LOADING STATE ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (errorMessage.isNotEmpty()) {
                // ── ERROR STATE ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
            } else if (lessonDetails != null) {
                // ── LESSON CONTENT ──
                val lesson = lessonDetails!!

                // Video/Media Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a1a))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Video Placeholder (in real app, use ExoPlayer)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(Color(0xFF333333), shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Video",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Video: ${lesson.durationMinutes} minutes",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            // Lesson Info
                            Text(
                                text = "About this lesson",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = lesson.description,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Topics/Content
                            if (lesson.content.isNotEmpty()) {
                                Text(
                                    text = "Topics covered",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Parse and display topics (assuming comma-separated in content)
                                val topics = lesson.content.split(",").map { it.trim() }
                                topics.forEach { topic ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Topic",
                                            tint = Color(0xFF22c55e),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = topic,
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── COMPLETION STATUS ──
                if (isCompleted) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a3a1a))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF22c55e),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Lesson completed successfully! 🎉",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Great progress towards mastery",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── ACTION BUTTONS ──
                Button(
                    onClick = { markLessonComplete() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    enabled = !isCompleted && !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (isCompleted) "✓ Lesson Completed" else "Mark as Complete",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Back", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}