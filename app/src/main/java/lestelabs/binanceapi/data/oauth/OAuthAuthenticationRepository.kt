package edu.uoc.pac4.data.oauth

import edu.uoc.pac4.data.oauth.datasource.OAuthRemoteDataSource
import edu.uoc.pac4.data.oauth.datasource.SessionManager

/**
 * Created by alex on 11/21/20.
 */
class OAuthAuthenticationRepository(
    private val localDataSource: SessionManager,
    private val remoteDataSource: OAuthRemoteDataSource,
) : AuthenticationRepository {

    override suspend fun isUserAvailable(): Boolean {
        return localDataSource.isUserAvailable()
    }

    override suspend fun login(authorizationCode: String): Boolean {
        // Get tokens from OAuth API
        val response = remoteDataSource.getTokens(authorizationCode)
        response?.accessToken?.let { accessToken ->
            // Save tokens locally
            localDataSource.saveAccessToken(accessToken)
            response.refreshToken?.let { refreshToken ->
                localDataSource.saveRefreshToken(refreshToken)
            }
            // Success
            return true
        } ?: run {
            // Couldn't get tokens from API
            // Failure
            return false
        }
    }

    override suspend fun logout() {
        // Clear Local Session Data
        localDataSource.clearAccessToken()
        localDataSource.clearRefreshToken()
    }
}