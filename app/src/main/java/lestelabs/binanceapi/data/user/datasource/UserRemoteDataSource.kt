package edu.uoc.pac4.data.user.datasource

import android.util.Log
import edu.uoc.pac4.data.network.Endpoints
import edu.uoc.pac4.data.network.UnauthorizedException
import edu.uoc.pac4.data.user.model.User
import edu.uoc.pac4.data.user.model.UsersResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by alex on 12/09/2020.
 */

class UserRemoteDataSource(private val client: HttpClient) {

    private val TAG = "UserRemoteDataSource"

    suspend fun getUser(): User? = withContext(Dispatchers.IO) {
        try {
            val response = client.get<UsersResponse>(Endpoints.usersUrl)
            response.data?.firstOrNull()
        } catch (t: Throwable) {
            (t as? ClientRequestException)?.let {
                if (it.response?.status == HttpStatusCode.Unauthorized) {
                    throw UnauthorizedException
                }
            }
            Log.d(TAG, "Error retrieving user", t)
            null
        }
    }

    suspend fun updateUser(description: String): User? = withContext(Dispatchers.IO) {
        try {
            val response = client.put<UsersResponse>(Endpoints.usersUrl) {
                parameter("description", description)
            }
            response.data?.firstOrNull()
        } catch (t: Throwable) {
            (t as? ClientRequestException)?.let {
                if (it.response?.status == HttpStatusCode.Unauthorized) {
                    throw UnauthorizedException
                }
            }
            Log.d(TAG, "Error updating user", t)
            null
        }
    }

}