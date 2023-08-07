package ca.sxxxi.titter.data.models

data class User(
	val id: String = "",
	val pfp: ByteArray? = null,
	val fName: String = "",
	val lName: String = "",
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as User

		if (id != other.id) return false
		if (pfp != null) {
			if (other.pfp == null) return false
			if (!pfp.contentEquals(other.pfp)) return false
		} else if (other.pfp != null) return false
		if (fName != other.fName) return false
		if (lName != other.lName) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + (pfp?.contentHashCode() ?: 0)
		result = 31 * result + fName.hashCode()
		result = 31 * result + lName.hashCode()
		return result
	}
}
