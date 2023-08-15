package ca.sxxxi.titter

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ca.sxxxi.titter.activeUser.ActiveUser
import ca.sxxxi.titter.proto.Settings
import ca.sxxxi.titter.ui.navigation.CommentsScreenArgs
import ca.sxxxi.titter.ui.navigation.commentsScreen
import ca.sxxxi.titter.ui.navigation.homeScreen
import ca.sxxxi.titter.ui.navigation.login
import ca.sxxxi.titter.ui.navigation.navigateToCommentsScreen
import ca.sxxxi.titter.ui.navigation.navigateToHome
import ca.sxxxi.titter.ui.navigation.navigateToLogin
import ca.sxxxi.titter.ui.navigation.navigateToPostCreate
import ca.sxxxi.titter.ui.navigation.navigateToSearch
import ca.sxxxi.titter.ui.navigation.navigateToSignup
import ca.sxxxi.titter.ui.navigation.postCreate
import ca.sxxxi.titter.ui.navigation.searchScreen
import ca.sxxxi.titter.ui.navigation.signup
import ca.sxxxi.titter.ui.screens.TestingScreen
import ca.sxxxi.titter.ui.theme.TitterTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import java.io.OutputStream

object UserPrefsSerializer : Serializer<Settings> {
	private const val TAG = "UserPrefsSerializer"
	override val defaultValue: Settings
		get() = Settings.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): Settings {
		try {
			return Settings.parseFrom(input)
		} catch (e: CorruptionException) {
			Log.e(TAG, "Input corrupted.")
			throw e
		}
	}

	override suspend fun writeTo(t: Settings, output: OutputStream) {
		t.writeTo(output)
	}

}

object ActiveUserSerializer : Serializer<ActiveUser> {
	private const val TAG = "ActiveUserSerializer"

	override val defaultValue: ActiveUser
		get() = ActiveUser.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): ActiveUser {
		try {
			return ActiveUser.parseFrom(input)
		} catch (e: CorruptionException) {
			Log.e(TAG, "Input corrupted.")
			throw e
		}
	}

	override suspend fun writeTo(t: ActiveUser, output: OutputStream) = t.writeTo(output)
}

val Context.activeUserDataStore by dataStore(
	fileName = "activeUser.pb",
	serializer = ActiveUserSerializer
)

val Context.userPrefsDataStore by dataStore(
	fileName = "userPrefs.pb",
	serializer = UserPrefsSerializer
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			TitterTheme {
				val navController = rememberNavController()

				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					NavHost(
						navController = navController,
//						startDestination = Screen.SearchScreen.route
						startDestination = NavGroup.Authorization.route
//						startDestination = Screen.SearchScreen.route
					) {
						navigation(
							route = NavGroup.Authorization.route,
							startDestination = Screen.Login.route,
						) {
							login(
								onNavigateToSignup = navController::navigateToSignup,
								onNavigateToHome = navController::navigateToHome
							)
							signup(navController::navigateToLogin)
						}
						homeScreen(
							onNavigateToPostCreate = navController::navigateToPostCreate,
							onNavigateToAuthentication = navController::navigateToLogin,
							onNavigateToComments = navController::navigateToCommentsScreen,
							onNavigateToSearch = navController::navigateToSearch
						)
						postCreate(onExitRequested = navController::popBackStack)
						commentsScreen(onBackPressed = navController::popBackStack)
						searchScreen()
					}
				}
			}
		}
	}
}

sealed class NavGroup(val route: String) {
	object Authorization : NavGroup("authorization")
}

sealed class Screen(
	val route: String,
	val routeWithArgs: String = route,
	val arguments: List<NamedNavArgument> = listOf()
) {
	object Login : Screen("login")
	object Signup : Screen("signup")
	object Home : Screen("home")
	object PostCreate : Screen("postCreate")
	object CommentsScreen : Screen(
		route = "comments",
		routeWithArgs = "comments/{${CommentsScreenArgs.POST_ID_ARG}}",
		arguments = listOf(navArgument(CommentsScreenArgs.POST_ID_ARG) { type = NavType.StringType })
	)
	object SearchScreen : Screen(route = "search")

}