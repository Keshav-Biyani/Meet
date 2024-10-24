package com.example.meetapp.utils

import android.content.Context
import androidx.credentials.GetCredentialRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.meetapp.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import java.security.MessageDigest
import java.util.UUID


object AuthUtils {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun getGoogleSignInClient(context: Context): GetCredentialRequest {
        val clientId = BuildConfig.Client_Id
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold(""){
                str,it->str+"%02x".format(it)
        }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)

            .setServerClientId(clientId)
            .setNonce(hashedNonce)
            .build()


        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return request
    }
}
