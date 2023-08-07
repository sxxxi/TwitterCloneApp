package ca.sxxxi.titter.ui.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.sharp.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.sxxxi.titter.BuildConfig
import ca.sxxxi.titter.R
import ca.sxxxi.titter.data.models.Post
import java.text.SimpleDateFormat

@Composable
fun PostCard(
	modifier: Modifier = Modifier,
	post: Post,
	onCommentsClicked: (String) -> Unit = {}
) {
	Column(
		modifier = modifier
			.padding(horizontal = 8.dp, vertical = 4.dp)
//			.border(
//				width = 1.dp,
//				brush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
//				shape = RoundedCornerShape(8)
//			)
			.clip(RoundedCornerShape(8))
	) {
		Column(
			modifier = Modifier
				.background(MaterialTheme.colorScheme.surface)
		) {
			Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)) {
				// Header
				PostHeader(post = post)
				// Content
				Box(Modifier.padding(12.dp)) {
					Text(text = post.content)
				}
			}

			// Buttons
			ButtonRow(onCommentsClicked = { onCommentsClicked(post.id.toString()) })

		}
	}
}

@Composable
fun PostHeader(post: Post) {
	val pfp = post.author.pfp?.let {
		BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
	}

	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			post.author.let { u ->
				val mod = Modifier
					.width(24.dp)
					.clip(RoundedCornerShape(100))
				pfp?.let {
					Image(modifier = mod, bitmap = it, contentDescription = null)
				} ?: Image(
					modifier = mod.then(Modifier.background(Color.Black)),
					painter = painterResource(id = R.drawable.skully),
					contentDescription = null
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = "${u.fName} ${u.lName}",
					style = MaterialTheme.typography.bodySmall + TextStyle(fontWeight = FontWeight.Bold)
				)
			}
		}
		Text(
			text = SimpleDateFormat.getDateInstance().format(post.dateTimeCreated),
			style = MaterialTheme.typography.bodySmall
		)

	}
}


@Composable
private fun ButtonRow(onCommentsClicked: () -> Unit) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		NumberedIcon(number = 0, imageProvider = Icons.Default.Share)
		NumberedIcon(
			number = 0,
			imageProvider = painterResource(id = R.drawable.skully),
			onClick = onCommentsClicked,

			)
		NumberedIcon(number = 0, imageProvider = Icons.Sharp.Favorite,
			onClick = {
				Log.d("TAG", "${BuildConfig.MY_API_KEY}")
			}
		)
	}
}



@Composable
fun <T : Any> NumberedIcon(
	number: Int,
	imageProvider: T,
	contentDescription: String? = null,
	iconWidth: Dp = 16.dp,
	onClick: () -> Unit = {},
	tint: Color = MaterialTheme.colorScheme.primary
) {
	IconButton(onClick = onClick) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			when (imageProvider) {
				is ImageVector -> {
					Icon(
						modifier = Modifier.width(iconWidth),
						tint = tint,
						imageVector = imageProvider,
						contentDescription = contentDescription
					)
				}

				is Painter -> {
					Icon(
						modifier = Modifier.width(iconWidth),
						tint = tint,
						painter = imageProvider,
						contentDescription = contentDescription
					)
				}
			}
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = number.toString(),
				style = MaterialTheme.typography.bodySmall + TextStyle(color = tint)
			)
		}
	}
}