package me.bmax.apatch.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.bmax.apatch.ui.theme.Win98Colors
import me.bmax.apatch.ui.theme.win98OutsetBorder

@Composable
fun WarningCard(
    message: String,
    color: Color? = null,
    onClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null
) {
    val bgColor = color ?: Win98Colors.ErrorBackground.copy(alpha = 0.15f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .win98OutsetBorder(borderWidth = 1.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .drawBehind { drawRect(bgColor) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
            } else {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Win98Colors.WindowText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Win98Colors.WindowText,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(Alignment.CenterVertically)
            )

            if (onClose != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(android.R.string.cancel),
                    tint = Win98Colors.WindowText,
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(16.dp)
                )
            }
        }
    }
}