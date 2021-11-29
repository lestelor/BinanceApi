package edu.uoc.pac4.data.user

import edu.uoc.pac4.data.user.datasource.UserRemoteDataSource
import edu.uoc.pac4.data.user.model.User

/**
 * Created by alex on 11/21/20.
 */

class TwitchUserRepository(
    private val remoteDataSource: UserRemoteDataSource,
) : UserRepository {

    override suspend fun getUser(): User? = remoteDataSource.getUser()

    override suspend fun updateUser(description: String): User? =
        remoteDataSource.updateUser(description)

}