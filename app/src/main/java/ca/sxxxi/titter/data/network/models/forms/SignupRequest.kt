package ca.sxxxi.titter.data.network.models.forms

data class SignupRequest(
	val username: String = "",
	val password: String = "",
	val pfp: String = "",
	val firstName: String = "",
	val lastName: String = ""
)