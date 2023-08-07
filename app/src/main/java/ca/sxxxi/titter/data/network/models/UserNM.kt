package ca.sxxxi.titter.data.network.models

import com.squareup.moshi.Json

data class UserNM(
	@field:Json(name = "id") val id: String,
//	@field:Json(name = "pfp") val pfp: String,
	@field:Json(name = "firstName") val firstName: String,
	@field:Json(name = "lastName") val lastName: String
)
