package com.mansi.aiinsight.ui.screens.course

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.mansi.aiinsight.data.model.Course
import com.mansi.aiinsight.data.repository.CourseRepository
import com.mansi.aiinsight.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun CourseSelectionScreen(
    navController: NavController,
    courseRepository: CourseRepository,
    authRepository: AuthRepository
) {
    var courses by remember { mutableStateOf<List<Course>>(emptyList()) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Function to load courses
    fun loadCourses() {
        isLoading = true
        errorMessage = ""
        scope.launch {
            try {
                val result = courseRepository.getCourses()
                result.onSuccess { courseList ->
                    courses = courseList
                    // Auto-select first course
                    if (selectedCourse == null && courseList.isNotEmpty()) {
                        selectedCourse = courseList[0]
                    }
                    isLoading = false
                    errorMessage = ""
                }
                result.onFailure { exception ->
                    errorMessage = exception.message ?: "Failed to load courses"
                    courses = emptyList()
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "An error occurred while loading courses"
                courses = emptyList()
                isLoading = false
            }
        }
    }

    // Initial load and refresh
    LaunchedEffect(refreshTrigger) {
        loadCourses()
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
            // ── HEADER ──
            Text(
                text = "Select Your Course Package",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Choose the plan that fits your learning goals",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // ── LOADING STATE ──
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            // ── ERROR STATE ──
            else if (errorMessage.isNotEmpty()) {
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
            // ── EMPTY STATE ──
            else if (courses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No courses available at the moment",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
            // ── COURSES LIST ──
            else {
                courses.forEach { course ->
                    CourseSelectionItem(
                        course = course,
                        isSelected = selectedCourse?.id == course.id,
                        onSelect = { selectedCourse = course }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── ACTION BUTTONS ──
            if (!isLoading && errorMessage.isEmpty() && courses.isNotEmpty()) {
                Button(
                    onClick = {
                        if (selectedCourse != null) {
                            isSubmitting = true
                            try {
                                courseRepository.saveSelectedCourse(selectedCourse!!.name)
                                // Navigate to payment with course id
                                navController.navigate("payment/${selectedCourse!!.id}") {
                                    // Optional: pop until a certain destination
                                }
                            } catch (e: Exception) {
                                errorMessage = "Failed to proceed: ${e.message}"
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    enabled = selectedCourse != null && !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Continue to Payment",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        authRepository.logout()
                        navController.navigate("landing") {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CourseSelectionItem(
    course: Course,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color.DarkGray else Color(0xFF1a1a1a),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color.White else Color.DarkGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row: Radio button and course name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.Gray
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = course.description.ifEmpty { "No description available" },
                        color = Color.Gray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${course.duration} days access",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )

                            if (course.level.isNotEmpty()) {
                                Text(
                                    text = "Level: ${course.level.uppercase()}",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Price and badge
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = if (course.amount > 0) "₹${course.amount}" else "FREE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Box(
                                modifier = Modifier
                                    .background(
                                        when {
                                            course.amount == 0 -> Color(0xFF166534)
                                            course.amount < 1000 -> Color(0xFF422006)
                                            else -> Color(0xFF500724)
                                        },
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = when {
                                        course.amount == 0 -> "FREE"
                                        course.amount < 1000 -> "BASIC"
                                        else -> "PREMIUM"
                                    },
                                    color = when {
                                        course.amount == 0 -> Color(0xFF4ADE80)
                                        course.amount < 1000 -> Color(0xFFDEDC02)
                                        else -> Color(0xFFF472B6)
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Selection indicator at bottom
            if (isSelected) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}