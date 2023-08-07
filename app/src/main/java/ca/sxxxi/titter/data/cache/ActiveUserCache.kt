package ca.sxxxi.titter.data.cache

import androidx.datastore.core.DataStore
import ca.sxxxi.titter.activeUser.ActiveUser

class ActiveUserCache(private val activeUserDataStore: DataStore<ActiveUser>) : Cachable<ActiveUser> {
	override val value = activeUserDataStore.data

	override suspend fun clear() {
		activeUserDataStore.updateData { it.toBuilder().clear().build() }
	}

	override suspend fun update(updater: (ActiveUser) -> ActiveUser) {
		activeUserDataStore.updateData { updater(it) }
	}
}