package com.mansi.aiinsight.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
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
import com.mansi.aiinsight.data.model.Module
import com.mansi.aiinsight.data.repository.ModuleRepository
import kotlinx.coroutines.launch

@Composable
fun LearningModuleScreen(
    navController: NavController,
    moduleRepository: ModuleRepository,
    courseLevel: String,
    courseId: Int
) {
    var modules by remember { mutableStateOf<List<Module>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Function to refresh modules
    fun refreshModules() {
        isLoading = true
        scope.launch {
            val result = moduleRepository.getModules(courseId)
            result.onSuccess {
                modules = it
                isLoading = false
                errorMessage = ""
            }
            result.onFailure {
                errorMessage = it.message ?: "Failed to load modules"
                isLoading = false
            }
        }
    }

    // Initial load and refresh
    LaunchedEffect(refreshTrigger) {
        refreshModules()
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
            Text(
                text = "${courseLevel.uppercase()} COURSE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Modules to unlock your potential",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                errorMessage.isNotEmpty() -> {
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
                }
                modules.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No modules available for this course",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    // Display modules dynamically
                    modules.forEachIndexed { index, module ->
                        LearningModuleCard(
                            moduleNumber = index + 1,
                            title = module.name,
                            description = module.description,
                            isCompleted = module.isCompleted,
                            isLocked = module.isLocked,
                            onClick = {
                                if (!module.isLocked && !module.isCompleted) {
                                    navController.navigate("lesson/${module.id}")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress Summary
                    val completedCount = modules.count { it.isCompleted }
                    val totalCount = modules.size
                    val progressPercent = if (totalCount > 0) (completedCount * 100) / totalCount else 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a2a1a))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Course Progress",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$completedCount / $totalCount",
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = progressPercent / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = Color(0xFF4ADE80),
                                trackColor = Color(0xFF1A3A1A),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$progressPercent% Complete",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoading && errorMessage.isEmpty()) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        "Back to Dashboard",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LearningModuleCard(
    moduleNumber: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !isLocked,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> Color(0xFF1A3A1A)
                isLocked -> Color(0xFF1A1A1A)
                else -> Color(0xFF1A1A1A)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                when {
                                    isCompleted -> Color(0xFF166534).copy(alpha = 0.3f)
                                    isLocked -> Color.DarkGray.copy(alpha = 0.3f)
                                    else -> Color.White.copy(alpha = 0.05f)
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                0.5.dp,
                                when {
                                    isCompleted -> Color(0xFF4ADE80).copy(alpha = 0.3f)
                                    isLocked -> Color.Gray.copy(alpha = 0.3f)
                                    else -> Color.White.copy(alpha = 0.1f)
                                },
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isCompleted -> Icons.Default.CheckCircle
                                isLocked -> Icons.Default.Lock
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = null,
                            tint = when {
                                isCompleted -> Color(0xFF4ADE80)
                                isLocked -> Color.Gray
                                else -> Color.White
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Module $moduleNumber",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = title,
                            color = when {
                                isLocked -> Color.Gray
                                else -> Color.White
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                    }
                }

                if (isLocked) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            if (isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF166534).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ Completed",
                        color = Color(0xFF4ADE80),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}