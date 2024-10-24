package com.example.meetapp.ui.viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.meetapp.MainActivity
import com.example.meetapp.R
import com.example.meetapp.data.repository.AuthRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.IdToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

class AuthViewModel(val authRepository: AuthRepository) : ViewModel() {

    private val _user = MutableStateFlow<GoogleCredentials?>(null)
    val user: StateFlow<GoogleCredentials?> = _user
    fun signIn(mainActivity: MainActivity) {

        viewModelScope.launch {
            authRepository.rensmae(mainActivity,
                onSuccess = { googleCredentials ->
                    _user.value = googleCredentials

                },
                onError = { e ->
                    e.printStackTrace()

                }

            )
        }}
    fun getCredential(): GoogleCredentials? {
             return user.value
}


    @Suppress("UNCHECKED_CAST")
    class MyViewModelFactory(
        private val repository: AuthRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }


    }
}

//         _user.value=   authRepository.rensmae()
//            val googleIdTokenCredential: GoogleIdTokenCredential = GoogleIdTokenCredential
//                .createFrom(_user.value!!.data)
//            idToken.value = googleIdTokenCredential.idToken
//            withContext(Dispatchers.IO) {
//                val transport: HttpTransport = NetHttpTransport()
//                val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
//                val verifier = GoogleIdTokenVerifier.Builder(
//                    transport,
//                    jsonFactory
//                ) // Specify the CLIENT_ID of the app that accesses the backend:
//                    .setAudience(listOf("1016051888783-7cen2jmdmsop2tjnqgh61uccpk30oifu.apps.googleusercontent.com")) // Or, if multiple clients access the backend:
//                    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                    .build()
//
//                // Verify the ID token in the background
//               //  idToken.value = verifier.verify(googleIdToken).payload.accessTokenHash
//        }

//    fun getGoogleCredentials(context: Context): GoogleCredentials? {
//
//        val accessToken = _user.value?.serverAuthCode
//        var googleCredentials: GoogleCredentials?= null
//        viewModelScope.launch {
//       // accessToken= GoogleAuthUtil.getToken(context,_user.value?.account!!,"https://www.googleapis.com/auth/meetings.space.created")
//
//
//           if(accessToken!= null){
//               val credentials = AccessToken(accessToken, null)
//               googleCredentials = GoogleCredentials.create(credentials)
//           }
//        }
//        return googleCredentials
//
//    }