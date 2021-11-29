package edu.uoc.pac4.data.oauth.datasource

import android.util.Log
import edu.uoc.pac4.data.network.Endpoints
import edu.uoc.pac4.data.oauth.util.OAuthConstants
import edu.uoc.pac4.data.oauth.model.OAuthTokensResponse
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by alex on 12/09/2020.
 */

class OAuthRemoteDataSource(private val client: HttpClient) {

    private val TAG = "OAuthRemoteDataSource"

    suspend fun getTokens(
        authorizationCode: String
    ): OAuthTokensResponse? = withContext(Dispatchers.IO) {
        try {
            val response = client.post<OAuthTokensResponse>(Endpoints.tokenUrl) {
                parameter("client_id", OAuthConstants.clientID)
                parameter("client_secret", OAuthConstants.clientSecret)
                parameter("code", authorizationCode)
                parameter("grant_type", "authorization_code")
                parameter("redirect_uri", OAuthConstants.redirectUri)
            }
            Log.d(TAG, "Got Access token ${response.accessToken}")
            response
        } catch (t: Throwable) {
            Log.w(TAG, "Error getting tokens", t)
            null
        }
    }


    suspend fun refreshToken(
        refreshToken: String
    ): OAuthTokensResponse? = withContext(Dispatchers.IO) {
        // Get Refresh Token
        try {
            // Launch Refresh Request
            val response = client.post<OAuthTokensResponse>(Endpoints.tokenUrl) {
                parameter("client_id", OAuthConstants.clientID)
                parameter("client_secret", OAuthConstants.clientSecret)
                parameter("refresh_token", refreshToken)
                parameter("grant_type", "refresh_token")
            }
            Log.d(TAG, "Got new Access token ${response.accessToken}")
            response
        } catch (t: Throwable) {
            Log.w(TAG, "Error refreshing tokens", t)
            null
        }

    }
}
