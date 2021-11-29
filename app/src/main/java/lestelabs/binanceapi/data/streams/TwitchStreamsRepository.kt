package edu.uoc.pac4.data.streams

import edu.uoc.pac4.data.streams.datasource.StreamsRemoteDataSource
import edu.uoc.pac4.data.streams.model.Stream

/**
 * Created by alex on 11/21/20.
 */

class TwitchStreamsRepository(
    private val remoteDataSource: StreamsRemoteDataSource,
    // We could expand this repository adding, for example, a local datasource
    // with a database, to load streams on app open
    // and provide offline functionality
) : StreamsRepository {

    override suspend fun getStreams(cursor: String?): Pair<String?, List<Stream>> {
        remoteDataSource.getStreams(cursor)?.let { response ->
            // Got Streams
            val streams = response.data.orEmpty()
            // Convert to Pair<Key, Value> to keep the twitch response and pagination
            // in the data layer
            return Pair(response.pagination?.cursor, streams)
        } ?: run {
            // No Streams Available
            return Pair(null, emptyList())
        }
    }

}