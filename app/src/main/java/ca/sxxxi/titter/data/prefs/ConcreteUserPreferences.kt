package ca.sxxxi.titter.data.prefs

import androidx.datastore.core.DataStore
import ca.sxxxi.titter.proto.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date

class ConcreteUserPreferences(private val prefs: DataStore<Settings>) : UserPreferences {
	override val latestRefreshDate: Flow<Long> = prefs.data.map { it.latestRefresh }
	override val currentPage: Flow<Int> = prefs.data.map { it.currentPage }
	override suspend fun setLatestRefreshDate(epochSeconds: Long) {
		prefs.updateData { it.toBuilder().setLatestRefresh(epochSeconds).build() }
	}

	override suspend fun refresh() {
		prefs.updateData {
			it.toBuilder()
				.setLatestRefresh(Date().toInstant().epochSecond)
				.setCurrentPage(1)
				.build()
		}

	}

	override suspend fun nextPage(): Int {
		return currentPage.first().let { page ->
			prefs.updateData { it.toBuilder().setCurrentPage(page + 1).build() }
			page + 1
		}
	}
}