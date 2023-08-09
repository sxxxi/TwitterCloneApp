package ca.sxxxi.titter.data.utils.mappers

import ca.sxxxi.titter.data.local.entities.UserEntity
import ca.sxxxi.titter.data.models.User
import ca.sxxxi.titter.data.network.models.UserNM
import ca.sxxxi.titter.data.utils.contracts.UserMapper

class UserMapperImpl : UserMapper {
	override fun networkToEntity(source: UserNM): UserEntity {
		return UserEntity(
			id = source.id,
			fName = source.firstName,
			lName = source.lastName,
		)
	}

	override fun entityToDomain(source: UserEntity): User {
		return User(
			id = source.id,
			fName = source.fName,
			lName = source.lName
		)
	}

	override fun networkToDomain(net: UserNM): User {
		return User(
			id = net.id,
			pfp = null,
			fName = net.firstName,
			lName = net.lastName
		)
	}
}