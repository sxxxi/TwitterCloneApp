package ca.sxxxi.titter.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ca.sxxxi.titter.ui.viewmodels.TestingViewModel

@Composable
fun TestingScreen(viewModel: TestingViewModel = hiltViewModel()) {
	Column {
		Button(onClick = { viewModel.getClosestPost() }) {
			Text(text = "First")	
		}
		Button(onClick = { viewModel.test() }) {
			Text(text = "Second")
		}
	}

}