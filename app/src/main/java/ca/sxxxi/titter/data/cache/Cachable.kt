package ca.sxxxi.titter.data.cache

import kotlinx.coroutines.flow.Flow

interface Cachable<T> {
	val value: Flow<T>
	suspend fun update(updater: (T) -> T)
	suspend fun clear()
}