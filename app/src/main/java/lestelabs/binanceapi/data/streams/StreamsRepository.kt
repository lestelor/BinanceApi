package lestelabs.binanceapi.data.streams

import lestelabs.binanceapi.data.network.UnauthorizedException
import lestelabs.binanceapi.data.streams.model.Stream

/**
 * Created by alex on 12/09/2020.
 */

interface StreamsRepository {
    /// Returns a Pair object containing
    /// first: Pagination cursor
    /// second: List of Streams
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): Pair<String?, List<Stream>>
}


