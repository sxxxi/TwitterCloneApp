package ca.sxxxi.titter.data.prefs

import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.Date

interface UserPreferences {
	val latestRefreshDate: Flow<Long>
	val currentPage: Flow<Int>
	suspend fun setLatestRefreshDate(epochSeconds: Long = Date().toInstant().epochSecond)
	suspend fun refresh()
	suspend fun nextPage(): Int
}