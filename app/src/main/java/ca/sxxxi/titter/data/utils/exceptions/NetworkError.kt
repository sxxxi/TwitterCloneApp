package ca.sxxxi.titter.data.utils.exceptions

sealed class NetworkError(msg: String) : Exception(msg) {
	class ConnectionFailed(msg: String) : NetworkError(msg)
}
