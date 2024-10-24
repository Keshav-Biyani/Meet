package com.example.meetapp.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.meetapp.MainActivity
import com.example.meetapp.data.repository.MeetRepository
import com.example.meetapp.ui.viewmodels.AuthViewModel
import com.example.meetapp.ui.viewmodels.MeetViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.runBlocking

@Composable
fun AuthScreen(viewModel: AuthViewModel,navController: NavController,mainActivity: MainActivity) {
    val context = LocalContext.current as Activity
    val user by viewModel.user.collectAsState()



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user == null) {
            Button(onClick = {
//                signInLauncher.launch(viewModel.authRepository.getSignInIntent())
                  viewModel.signIn(mainActivity)
            }) {
                Text("Sign in with Google")
            }
        } else {
            LaunchedEffect(user) {
                // Navigate to the MeetScreen once the user is signed in
                navController.navigate("meet") {
                    popUpTo("auth") { inclusive = true }
                }
            }

        }
    }
}
//    val signInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//        Log.e("Data12345",task.toString())
//        viewModel.signIn(context, task)
//    }
