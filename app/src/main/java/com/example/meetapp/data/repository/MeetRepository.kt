package com.example.meetapp.data.repository

import android.util.Log
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.apps.meet.v2.CreateSpaceRequest
import com.google.apps.meet.v2.Space
import com.google.apps.meet.v2.SpacesServiceClient
import com.google.apps.meet.v2.SpacesServiceSettings
import com.google.auth.Credentials
import kotlinx.coroutines.coroutineScope
import java.io.IOException


class MeetRepository(private val credentials: Credentials) {

    suspend fun createGoogleMeetSpace(onSuccess: (Space) -> Unit, onError: (Exception) -> Unit) {
        coroutineScope {
            try {
                // Create Google Meet space
                val settings = SpacesServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build()
                Log.e("Success", settings.toString())
                val request = CreateSpaceRequest.newBuilder()
                    .setSpace(Space.newBuilder().build())
                    .build()
                Log.e("SuccessRequest", request.toString())
                val client = SpacesServiceClient.create(settings)
                Log.e("SuccessClient", client.toString())
                val response = client.createSpace(request)
                Log.e("Response", response.toString())
                onSuccess(response)
            } catch (e: IOException) {
                Log.e("SuccessRequest", "HELO")
                onError(e)
            }
        }
    }
}