package com.example.meetapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meetapp.data.repository.AuthRepository
import com.example.meetapp.data.repository.MeetRepository
import com.example.meetapp.ui.screens.AuthScreen
import com.example.meetapp.ui.screens.MeetScreen
import com.example.meetapp.ui.theme.MeetAppTheme
import com.example.meetapp.ui.viewmodels.AuthViewModel
import com.example.meetapp.ui.viewmodels.MeetViewModel
import com.example.meetapp.utils.AuthUtils
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.apps.meet.v2.CreateSpaceRequest
import com.google.apps.meet.v2.Space
import com.google.apps.meet.v2.SpacesServiceClient
import com.google.apps.meet.v2.SpacesServiceSettings
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID


class MainActivity : ComponentActivity() {
    var credential : GoogleCredentials? = null
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZE) {
            val authorizationResult = Identity.getAuthorizationClient(
                this
            ).getAuthorizationResultFromIntent(data)

            val accessToken = AccessToken(authorizationResult.accessToken,null)
            credential = GoogleCredentials.create(accessToken)
           // saveToDriveAppFolder(authorizationResult)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Sign-in logic


        enableEdgeToEdge()
        setContent {


            MeetAppTheme {
                val context = LocalContext.current

                val authRepository = AuthRepository(AuthUtils.getGoogleSignInClient(this),this)

                // Initialize ViewModel without Hilt
                val authViewModel = ViewModelProvider(
                    this,
                    AuthViewModel.MyViewModelFactory(authRepository)
                )[AuthViewModel::class.java]


                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") {
                            // Pass the navigation controller to the AuthScreen
                            AuthScreen(viewModel = authViewModel, navController = navController,this@MainActivity)
                        }
                        composable("meet") {
                            // Use runBlocking to get credentials from AuthViewModel

                             val credentials= authViewModel.getCredential()
                                Log.e("Credential",credentials.toString())




                            if (credentials != null) {
                                // Initialize MeetRepository and MeetViewModel with the credentials
                                val meetRepository = MeetRepository(credentials)
                                val meetViewModel =
                                    ViewModelProvider(
                                        this@MainActivity,
                                        MeetViewModel.MeetViewModelFactory(meetRepository)
                                    )[MeetViewModel::class.java]

                                // Show MeetScreen and pass the initialized MeetViewModel
                                MeetScreen(meetViewModel)
                            }else{
                                val meetRepository = MeetRepository(credential!!)
                                val meetViewModel =
                                    ViewModelProvider(
                                        this@MainActivity,
                                        MeetViewModel.MeetViewModelFactory(meetRepository)
                                    )[MeetViewModel::class.java]

                                // Show MeetScreen and pass the initialized MeetViewModel
                                MeetScreen(meetViewModel)

                            }
                        }
                    }


            }
        }
    }

    companion object {
        val REQUEST_AUTHORIZE =144
    }
}




@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun SignInWithGoogle(mainActivity: MainActivity) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    val hashedNonce = digest.fold(""){
        str,it->str+"%02x".format(it)
    }

    val onClick: () -> Unit = {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("1016051888783-7cen2jmdmsop2tjnqgh61uccpk30oifu.apps.googleusercontent.com")
            .setNonce(hashedNonce)
            .build()
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Launch coroutine on the main scope but switch the context to IO for network tasks
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)


                // Switch to Dispatchers.IO for network tasks
                withContext(Dispatchers.IO) {

                    val requestedScopes = listOf(
                        Scope("https://www.googleapis.com/auth/meetings.space.created")
                    )
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
                                val settings = SpacesServiceSettings.newBuilder()
                                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                                    .build()
                                Log.e("Success",settings.toString())
                                val request = CreateSpaceRequest.newBuilder()
                                    .setSpace(Space.newBuilder().build())
                                    .build()
                                Log.e("SuccessRequest",request.toString())
                                val client = SpacesServiceClient.create(settings)
                                Log.e("SuccessClient",client.toString())
                                val response = client.createSpace(request)
                                Log.e("Response",response.toString())
                                //val meetRepository = MeetRepository(credentials)


                               // saveToDriveAppFolder(authorizationResult)
                            }
                        }
                        .addOnFailureListener { e -> Log.e(TAG, "Failed to authorize", e) }
                    withContext(Dispatchers.Main) {

                    //    Log.e("SUCCESS", pendingIntent.toString())
                       // Toast.makeText(context,pendingIntent.toString(),Toast.LENGTH_LONG).show()

                    }
                }
            } catch (e: GetCredentialException) {
                // Handle exception
                Log.e("ERROR", "Failed to get credential: ${e.message}")
            } catch (e: Exception) {
                Log.e("ERROR", "An error occurred: ${e.message}")
            }
        }
    }


    Button(onClick = onClick) {
        Text(text = "Sign IN With google")
        
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MeetAppTheme {
        Greeting("Android")
    }
}
//val transport: HttpTransport = NetHttpTransport()
//                    val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
//                    val tokenRequest=  GoogleAuthorizationCodeTokenRequest(transport,jsonFactory,"https://oauth2.googleapis.com/token",
//                        "1016051888783-7cen2jmdmsop2tjnqgh61uccpk30oifu.apps.googleusercontent.com",
//                        "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",
//                        googleIdToken,
//                        "")
//    val tokenRespose = tokenRequest.execute()

//                    val verifier = GoogleIdTokenVerifier.Builder(
//                        transport,
//                        jsonFactory
//                    ) // Specify the CLIENT_ID of the app that accesses the backend:
//                        .setAudience(listOf("1016051888783-7cen2jmdmsop2tjnqgh61uccpk30oifu.apps.googleusercontent.com","1016051888783-cp62afcpqj9g4rddr3a393c0lblc0g6d.apps.googleusercontent.com")) // Or, if multiple clients access the backend:
//                        //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                        .build()
//
//                    // Verify the ID token in the background
//                    val idToken: GoogleIdToken? = verifier.verify(googleIdToken)
//                    val name = idToken?.payload?.accessTokenHash
//val nam = idToken?.payload?.get("name")
// Switch back to Main thread for UI updates
