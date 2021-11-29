package edu.uoc.pac4.data.streams.datasource

import edu.uoc.pac4.data.network.Endpoints
import edu.uoc.pac4.data.network.UnauthorizedException
import edu.uoc.pac4.data.streams.model.StreamsResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by alex on 12/09/2020.
 */

class StreamsRemoteDataSource(private val client: HttpClient) {

    suspend fun getStreams(cursor: String?): StreamsResponse? = withContext(Dispatchers.IO) {
        try {
            val response = client.get<StreamsResponse?>(Endpoints.streamsUrl) {
                cursor?.let { parameter("after", it) }
            }
            response
        } catch (t: Throwable) {
            (t as? ClientRequestException)?.let {
                if (it.response?.status == HttpStatusCode.Unauthorized) {
                    throw UnauthorizedException
                }
            }
            null
        }
    }
}