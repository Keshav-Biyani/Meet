package com.example.meetapp.data.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.meetapp.MainActivity
import com.example.meetapp.ui.screens.MeetScreen
import com.example.meetapp.ui.viewmodels.MeetViewModel
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.auth.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.coroutineScope as coroutineScope

import com.google.auth.oauth2.UserAuthorizer
import com.google.auth.oauth2.ClientId
import com.google.auth.oauth2.DefaultPKCEProvider
import com.google.auth.oauth2.TokenStore

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.apps.meet.v2.CreateSpaceRequest
import com.google.apps.meet.v2.Space
import com.google.apps.meet.v2.SpacesServiceClient
import com.google.apps.meet.v2.SpacesServiceSettings
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class AuthRepository(
    private val request: GetCredentialRequest,
    private val context : Context) {


 suspend   fun rensmae(mainActivity: MainActivity, onSuccess: (GoogleCredentials) -> Unit, onError: (Exception) -> Unit): Credential? {
    val credentialManager= CredentialManager.create(context)
    var credential : Credential ? = null
     val requestedScopes = listOf(
         Scope("https://www.googleapis.com/auth/meetings.space.created")
     )
    coroutineScope {
        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context,
            )
           credential =  result.credential

            val googleIdTokenCredential: GoogleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential!!.data)
            val googleIdToken = googleIdTokenCredential.idToken
            withContext(Dispatchers.IO) {
                val authorizationRequest =
                    AuthorizationRequest.builder().setRequestedScopes(requestedScopes).build()
                Identity.getAuthorizationClient(context)
                    .authorize(authorizationRequest)
                    .addOnSuccessListener { authorizationResult ->
                        if (authorizationResult.hasResolution()) {
                            // Access needs to be granted by the user
                            val pendingIntent   =
                                authorizationResult.pendingIntent
                            try {
                                startIntentSenderForResult(mainActivity,
                                    pendingIntent?.intentSender!!,
                                    144, null, 0, 0, 0, null
                                )
                            } catch (e: SendIntentException) {
                                Log.e(
                                    TAG,
                                    "Couldn't start Authorization UI: " + e.localizedMessage
                                )
                            }
                        } else {
                            // Access already granted, continue with user action
                            Log.e("Hello",authorizationResult.accessToken.toString())
                            val accessToken = AccessToken(authorizationResult.accessToken,null)
                            val credentials = GoogleCredentials.create(accessToken)
                            onSuccess(credentials)
                         //    Initialize MeetRepository and MeetViewModel with the credentials


                            // saveToDriveAppFolder(authorizationResult)
                        }
                    }
                    .addOnFailureListener { e -> Log.e(TAG, "Failed to authorize", e)
                        onError(e)
                    }


                // Switch back to Main thread for UI updates
                withContext(Dispatchers.Main) {

                }
            }


         //   handleSignIn(result)
        } catch (e: GetCredentialException) {
           // handleFailure(e)
        }

    }
    return credential

}
//    fun getSignInIntent(): android.content.Intent {
//
//        return googleSignInClient.signInIntent
//    }


//    fun handleSignInResult(task: Task<GoogleSignInAccount>, onSuccess: (GoogleSignInAccount) -> Unit, onError: (Exception) -> Unit) {
//        try {
//            val account = task.getResult(Exception::class.java)
//            account?.let { onSuccess(it) }
//        } catch (e: Exception) {
//            onError(e)
//        }
//    }
}