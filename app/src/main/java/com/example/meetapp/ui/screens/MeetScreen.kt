package com.example.meetapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.meetapp.ui.viewmodels.MeetViewModel


@Composable
fun MeetScreen(viewModel: MeetViewModel) {
    val meetSpace by viewModel.meetSpace.collectAsState()
    val context = LocalContext.current
    val meetUri = meetSpace?.meetingUri

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (meetSpace == null) {
                    viewModel.createMeetSpace()
                } else {
                    meetUri?.let { uri ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        context.startActivity(intent)
                    }
                }
            }
        ) {
            Text(if (meetSpace == null) "Create Google Meet Space" else "Join Google Meet Space")
        }
    }

    // Open Google Meet automatically once meetSpace is created

    LaunchedEffect(meetSpace) {
        meetUri?.let { uri ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            context.startActivity(intent)
        }
    }
}
