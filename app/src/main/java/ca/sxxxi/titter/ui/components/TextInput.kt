package ca.sxxxi.titter.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

sealed class FieldValidationStatus(val color: Color = Color.Transparent) {
	class Invalid(color: Color = Color.Red) : FieldValidationStatus(color)
	class Valid(color: Color = Color.Green) : FieldValidationStatus(color)
	object Neutral : FieldValidationStatus(Color.Transparent)
}

data class BorderColor(val focused: Color, val unfocused: Color)

@Composable
fun TextInput(
	modifier: Modifier = Modifier,
	label: String = "",
	value: String,
	onValueChange: (String) -> Unit,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	singleLine: Boolean = true,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	required: Boolean = false,
	validationStatus: FieldValidationStatus = FieldValidationStatus.Neutral,
	borderColor: BorderColor = BorderColor(
		focused = MaterialTheme.colorScheme.outline,
		unfocused = MaterialTheme.colorScheme.outlineVariant
	),
	onFocusOut: () -> Unit = {},
) {
	var validationStatusState by remember { mutableStateOf(validationStatus) }
	val interactionSource = remember { MutableInteractionSource() }
	val textStyle = MaterialTheme.typography.bodyLarge +
			TextStyle(color = MaterialTheme.colorScheme.onSurface)
	val isFocused by interactionSource.collectIsFocusedAsState()
	val inputShape by animateIntAsState(
		targetValue = if (isFocused) 30 else 100,
		animationSpec = spring(dampingRatio = 1f)
	)
	val animatedBorderColor by animateColorAsState(
		targetValue = when (validationStatusState) {
			is FieldValidationStatus.Neutral ->
				if (isFocused)
					borderColor.focused
				else
					borderColor.unfocused
			else -> validationStatusState.color
		}
	)

	/*
	 * validationStatusState reverts back to neutral on input change but
	 * must also be set back to validationStatus when it changes
	 */
	LaunchedEffect(key1 = validationStatus) {
		validationStatusState = validationStatus
	}

	LaunchedEffect(key1 = isFocused) {
		if (!isFocused) onFocusOut()
	}

	BasicTextField(
		modifier = Modifier
			.padding(8.dp)
			.clip(RoundedCornerShape(inputShape)),
		interactionSource = interactionSource,
		keyboardOptions = keyboardOptions,
		value = value,
		onValueChange = {
			onValueChange(it)
			validationStatusState = FieldValidationStatus.Neutral
		},
		singleLine = singleLine,
		textStyle = textStyle,
		visualTransformation = visualTransformation,
		cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
		decorationBox = {
			Box(
				modifier = Modifier
					.focusable(
						enabled = true,
						interactionSource = interactionSource
					)
					.defaultMinSize(minWidth = 300.dp, minHeight = 64.dp)
					.height(32.dp)
					.background(MaterialTheme.colorScheme.surface)
					.fillMaxWidth()
				,
				contentAlignment = Alignment.CenterStart,
			) {
				Box(modifier = Modifier.padding(horizontal = 24.dp)) {
					if (value.isEmpty()) Text(text = label, style = textStyle)
					it()
				}
			}
		},
	)
}

@Composable
fun CreditCardInput(
	modifier: Modifier = Modifier,
	label: String = "",
	value: String,
	onValueChange: (String) -> Unit,
) {
	val creditCardOffsetTranslator = object : OffsetMapping {
		override fun originalToTransformed(offset: Int): Int {
			return when {
				offset < 4 -> offset
				offset < 8 -> offset + 1
				offset < 12 -> offset + 2
				offset <= 16 -> offset + 3
				else -> 19
			}
		}

		override fun transformedToOriginal(offset: Int): Int {
			return when {
				offset <= 4 -> offset
				offset <= 9 -> offset - 1
				offset <= 14 -> offset - 2
				offset <= 19 -> offset - 3
				else -> 16
			}
		}
	}
	val visualTransformation = VisualTransformation { text ->
		val trimmed = if (text.length > 16) text.text.substring(0..15) else text.text
		var newText = ""
		trimmed.forEachIndexed { index, char ->
			newText += char
			if ((index + 1) % 4 == 0 && index != 15) newText += "-"
		}
		TransformedText(AnnotatedString(newText), creditCardOffsetTranslator)
	}
	TextInput(
		modifier = modifier,
		label = label,
		value = value,
		onValueChange = onValueChange,
		keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
		visualTransformation = visualTransformation,
	)
}

@Composable
fun PasswordInput(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	label: String = "",
	validationStatus: FieldValidationStatus = FieldValidationStatus.Neutral,
	onFocusOut: () -> Unit = {}
) {
	val visualTransformation = VisualTransformation { text ->
		var newString = ""
		repeat(text.length) { newString += '*' }
		TransformedText(AnnotatedString(newString), OffsetMapping.Identity)
	}
	TextInput(
		modifier = modifier,
		value = value,
		onValueChange = onValueChange,
		label = label,
		visualTransformation = visualTransformation,
		onFocusOut = onFocusOut,
		validationStatus = validationStatus
	)
}