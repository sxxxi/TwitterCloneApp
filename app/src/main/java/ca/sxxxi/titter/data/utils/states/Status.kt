package ca.sxxxi.titter.data.utils.states

sealed class Status {
	object Neutral : Status()
	object Success : Status()
	object Loading : Status()
	object Failure : Status()

}
