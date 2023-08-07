package ca.sxxxi.titter.data.utils.exceptions

sealed class AuthenticationException(msg: String) : Exception(msg) {
	class LoginFailed(msg: String) : AuthenticationException(msg)
}