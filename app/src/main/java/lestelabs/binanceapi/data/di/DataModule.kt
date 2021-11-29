package edu.uoc.pac4.data.di

import edu.uoc.pac4.data.network.Network
import edu.uoc.pac4.data.oauth.AuthenticationRepository
import edu.uoc.pac4.data.oauth.OAuthAuthenticationRepository
import edu.uoc.pac4.data.oauth.datasource.OAuthRemoteDataSource
import edu.uoc.pac4.data.oauth.datasource.SessionManager
import edu.uoc.pac4.data.streams.StreamsRepository
import edu.uoc.pac4.data.streams.TwitchStreamsRepository
import edu.uoc.pac4.data.streams.datasource.StreamsRemoteDataSource
import edu.uoc.pac4.data.user.TwitchUserRepository
import edu.uoc.pac4.data.user.UserRepository
import edu.uoc.pac4.data.user.datasource.UserRemoteDataSource
import io.ktor.client.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by alex on 11/21/20.
 */

val dataModule = module {
    // Data Dependencies

    // HttpClient
    factory<HttpClient> {
        Network.createHttpClient(context = androidContext())
    }
    // Authentication
    factory { SessionManager(context = androidContext()) }
    factory { OAuthRemoteDataSource(client = get()) }
    single<AuthenticationRepository> {
        OAuthAuthenticationRepository(localDataSource = get(), remoteDataSource = get())
    }
    // Streams
    factory { StreamsRemoteDataSource(client = get()) }
    single<StreamsRepository> {
        TwitchStreamsRepository(remoteDataSource = get())
    }
    // User
    factory { UserRemoteDataSource(client = get()) }
    single<UserRepository> {
        TwitchUserRepository(remoteDataSource = get())
    }
}