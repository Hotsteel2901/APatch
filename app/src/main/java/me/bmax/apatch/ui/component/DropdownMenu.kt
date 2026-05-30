package me.bmax.apatch.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun ProvideMenuShape(
    value: RoundedCornerShape = RoundedCornerShape(0.dp),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = value),
        content = content
    )
}