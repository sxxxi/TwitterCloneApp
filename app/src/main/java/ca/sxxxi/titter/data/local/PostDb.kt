package ca.sxxxi.titter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.sxxxi.titter.data.local.dao.PostsDao
import ca.sxxxi.titter.data.local.entities.CommentEntity
import ca.sxxxi.titter.data.local.entities.PostEntity
import ca.sxxxi.titter.data.local.entities.UserEntity
import dagger.Provides

@Database(entities = [UserEntity::class, PostEntity::class, CommentEntity::class], version = 1)
abstract class PostDb : RoomDatabase() {
	abstract fun postDao(): PostsDao
	companion object {
		fun dao(context: Context): PostsDao {
			return Room
				.databaseBuilder(
					context,
					PostDb::class.java,
					"posts.db"
				)
				.build()
				.postDao()
		}
	}
}