package com.mansi.aiinsight.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mansi.aiinsight.data.model.UserProgress
import com.mansi.aiinsight.data.repository.ProgressRepository

@Composable
fun LearningModuleScreen(
    navController: NavController,
    progressRepository: ProgressRepository,
    level: String
) {

    var progressData by remember {
        mutableStateOf<UserProgress?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    val modules = when(level){

        "beginner" ->
            listOf("Beginner Level")

        "intermediate" ->
            listOf("Intermediate Level")

        "advanced" ->
            listOf("Advanced Level")

        else ->
            listOf("Beginner Level")
    }

    LaunchedEffect(Unit){

        val result =
            progressRepository.getProgress()

        result.onSuccess {

            progressData = it
            isLoading = false

        }

        result.onFailure {

            isLoading = false

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ){

            Text(
                text =
                    "${level.uppercase()} COURSE",

                fontSize = 24.sp,

                fontWeight =
                    FontWeight.Bold,

                color = Color.White
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            if(isLoading){

                CircularProgressIndicator()

            }else{

                modules.forEachIndexed {
                        index,
                        module ->

                    val completed =
                        progressData
                            ?.completedLevels
                            ?.contains(module)
                            ?: false

                    LearningModuleCard(

                        title = module,

                        isCompleted =
                            completed,

                        isLocked =
                            false,

                        onClick = {

                            navController
                                .navigate(
                                    "lesson/$index"
                                )

                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Button(

                onClick = {

                    navController
                        .popBackStack()

                },

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                Color.White
                        )

            ){

                Text(
                    "Back",
                    color = Color.Black
                )

            }

        }
    }
}


@Composable
fun LearningModuleCard(

    title:String,

    isCompleted:Boolean,

    isLocked:Boolean,

    onClick:()->Unit

){

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(100.dp),

        onClick =
            onClick,

        enabled =
            !isLocked,

        colors =
            CardDefaults
                .cardColors(

                    containerColor =
                        if(isCompleted)
                            Color(0xFF1A3A1A)

                        else
                            Color(0xFF1A1A1A)
                )

    ){

        Row(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ){

            Icon(

                imageVector =

                    if(isCompleted)
                        Icons.Default.CheckCircle

                    else if(isLocked)
                        Icons.Default.Lock

                    else
                        Icons.Default.PlayArrow,

                contentDescription =
                    null,

                tint =
                    Color.White

            )

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            Text(

                text =
                    title,

                color =
                    Color.White,

                fontWeight =
                    FontWeight.Bold

            )

        }

    }

}