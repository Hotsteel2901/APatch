package me.bmax.apatch.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.bmax.apatch.R
import me.bmax.apatch.ui.theme.Win98Button
import me.bmax.apatch.ui.theme.Win98Colors

@Composable
fun ModuleUpdateButton(
    onClick: () -> Unit
) = Win98Button(
    onClick = onClick, enabled = true, contentPadding = PaddingValues(8.dp)
) {
    Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = R.drawable.device_mobile_down),
        contentDescription = stringResource(id = R.string.apm_update),
        tint = Win98Colors.WindowText
    )
}

@Composable
fun ModuleRemoveButton(
    enabled: Boolean, onClick: () -> Unit
) = Win98Button(
    onClick = onClick, enabled = enabled, contentPadding = PaddingValues(8.dp)
) {
    Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = R.drawable.trash),
        contentDescription = stringResource(id = R.string.apm_remove),
        tint = if (enabled) Win98Colors.WindowText else Win98Colors.GrayText
    )
}

@Composable
fun ModuleUndoRemoveButton(
    enabled: Boolean, onClick: () -> Unit
) = Win98Button(
    onClick = onClick, enabled = enabled, contentPadding = PaddingValues(8.dp)
) {
    Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = R.drawable.undo),
        contentDescription = stringResource(id = R.string.apm_undo),
        tint = if (enabled) Win98Colors.WindowText else Win98Colors.GrayText
    )
}

@Composable
fun KPModuleRemoveButton(
    enabled: Boolean, onClick: () -> Unit
) = Win98Button(
    onClick = onClick, enabled = enabled, contentPadding = PaddingValues(8.dp)
) {
    Icon(
        modifier = Modifier.size(18.dp),
        painter = painterResource(id = R.drawable.trash),
        contentDescription = stringResource(id = R.string.kpm_unload),
        tint = if (enabled) Win98Colors.WindowText else Win98Colors.GrayText
    )
}

@Composable
fun ModuleStateIndicator(
    @DrawableRes icon: Int, color: Color = Win98Colors.GrayText
) {
    Image(
        modifier = Modifier.requiredSize(150.dp),
        painter = painterResource(id = icon),
        contentDescription = null,
        alpha = 0.1f,
        colorFilter = ColorFilter.tint(color)
    )
}