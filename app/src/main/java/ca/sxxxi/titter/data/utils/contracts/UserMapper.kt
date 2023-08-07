package ca.sxxxi.titter.data.utils.contracts

import ca.sxxxi.titter.data.local.entities.UserEntity
import ca.sxxxi.titter.data.models.User
import ca.sxxxi.titter.data.network.models.UserNM

interface UserMapper : Mapper<UserNM, UserEntity, User>, NDMapper<UserNM, User>