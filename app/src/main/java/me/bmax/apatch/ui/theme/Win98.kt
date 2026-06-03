package me.bmax.apatch.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Win98Colors {
    val Background = Color(0xFFC0C0C0)
    val Surface = Color(0xFFFFFFFF)
    val TitleBar = Color(0xFF000080)
    val TitleBarInactive = Color(0xFF808080)
    val TitleBarText = Color(0xFFFFFFFF)
    val ButtonFace = Color(0xFFC0C0C0)
    val ButtonHighlight = Color(0xFFFFFFFF)
    val ButtonLight = Color(0xFFDFDFDF)
    val ButtonShadow = Color(0xFF808080)
    val ButtonDarkShadow = Color(0xFF000000)
    val WindowText = Color(0xFF000000)
    val SelectedBackground = Color(0xFF000080)
    val SelectedText = Color(0xFFFFFFFF)
    val GrayText = Color(0xFF808080)
    val Desktop = Color(0xFF008080)
    val ActiveBorder = Color(0xFFC0C0C0)
    val TooltipBackground = Color(0xFFFFFFE1)
    val CloseButtonFace = Color(0xFFC0C0C0)
    val CloseButtonHover = Color(0xFFDF0000)
    val LinkColor = Color(0xFF0000FF)
    val ErrorBackground = Color(0xFFFF0000)
    val DividerLight = Color(0xFFDFDFDF)
    val DividerDark = Color(0xFF808080)
}

fun Modifier.win98OutsetBorder(
    topLeftOuter: Color = Win98Colors.ButtonHighlight,
    topLeftInner: Color = Win98Colors.ButtonLight,
    bottomRightInner: Color = Win98Colors.ButtonShadow,
    bottomRightOuter: Color = Win98Colors.ButtonDarkShadow,
    borderWidth: Dp = 1.dp
): Modifier = this.drawBehind {
    val w = size.width
    val h = size.height
    val bw = borderWidth.toPx()

    drawLine(topLeftOuter, Offset(0f, 0f), Offset(w, 0f), bw)
    drawLine(topLeftOuter, Offset(0f, 0f), Offset(0f, h), bw)
    drawLine(topLeftInner, Offset(bw, bw), Offset(w - bw, bw), bw)
    drawLine(topLeftInner, Offset(bw, bw), Offset(bw, h - bw), bw)
    drawLine(bottomRightOuter, Offset(w, h), Offset(0f, h), bw)
    drawLine(bottomRightOuter, Offset(w, h), Offset(w, 0f), bw)
    drawLine(bottomRightInner, Offset(w - bw, h - bw), Offset(bw, h - bw), bw)
    drawLine(bottomRightInner, Offset(w - bw, h - bw), Offset(w - bw, bw), bw)
}

fun Modifier.win98InsetBorder(
    topLeftOuter: Color = Win98Colors.ButtonShadow,
    topLeftInner: Color = Win98Colors.ButtonDarkShadow,
    bottomRightInner: Color = Win98Colors.ButtonLight,
    bottomRightOuter: Color = Win98Colors.ButtonHighlight,
    borderWidth: Dp = 1.dp
): Modifier = this.drawBehind {
    val w = size.width
    val h = size.height
    val bw = borderWidth.toPx()

    drawLine(topLeftOuter, Offset(0f, 0f), Offset(w, 0f), bw)
    drawLine(topLeftOuter, Offset(0f, 0f), Offset(0f, h), bw)
    drawLine(topLeftInner, Offset(bw, bw), Offset(w - bw, bw), bw)
    drawLine(topLeftInner, Offset(bw, bw), Offset(bw, h - bw), bw)
    drawLine(bottomRightOuter, Offset(w, h), Offset(0f, h), bw)
    drawLine(bottomRightOuter, Offset(w, h), Offset(w, 0f), bw)
    drawLine(bottomRightInner, Offset(w - bw, h - bw), Offset(bw, h - bw), bw)
    drawLine(bottomRightInner, Offset(w - bw, h - bw), Offset(w - bw, bw), bw)
}

fun Modifier.win98Divider(): Modifier = this.drawBehind {
    val w = size.width
    val h = size.height
    drawLine(Win98Colors.ButtonShadow, Offset(0f, 0f), Offset(w, 0f), 1f)
    drawLine(Win98Colors.ButtonHighlight, Offset(0f, 1f), Offset(w, 1f), 1f)
}

@Composable
fun Win98Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    content: @Composable RowScope.() -> Unit
) {
    val bgColor = if (enabled) Win98Colors.ButtonFace else Win98Colors.ButtonFace.copy(alpha = 0.6f)
    val contentColor = if (enabled) Win98Colors.WindowText else Win98Colors.GrayText

    Box(
        modifier = modifier
            .win98OutsetBorder()
            .win98OutsetBorder(
                topLeftOuter = Color.Transparent,
                topLeftInner = Win98Colors.ButtonLight,
                bottomRightInner = Win98Colors.ButtonShadow,
                bottomRightOuter = Color.Transparent,
                borderWidth = 1.dp
            )
            .win98BgColor(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

private fun Modifier.win98BgColor(color: Color): Modifier = this.drawBehind {
    drawRect(color)
}

@Composable
fun Win98CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "Cancel"
) {
    Win98Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun Win98OkButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "OK"
) {
    Win98Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun Win98Window(
    title: String,
    modifier: Modifier = Modifier,
    active: Boolean = true,
    showCloseButton: Boolean = false,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val titleBarColor = if (active) Win98Colors.TitleBar else Win98Colors.TitleBarInactive

    Column(
        modifier = modifier
            .win98OutsetBorder(borderWidth = 2.dp)
            .win98BgColor(Win98Colors.Background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .win98BgColor(titleBarColor)
                .padding(start = 4.dp, top = 2.dp, end = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Win98Colors.TitleBarText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (showCloseButton && onClose != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .win98OutsetBorder(borderWidth = 1.dp)
                        .win98BgColor(Win98Colors.ButtonFace)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\u00D7",
                        color = Win98Colors.WindowText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .then(contentModifier),
            content = content
        )
    }
}

private val contentModifier: Modifier = Modifier

@Composable
fun Win98TitleBar(
    title: String,
    modifier: Modifier = Modifier,
    active: Boolean = true,
    showCloseButton: Boolean = false,
    onClose: (() -> Unit)? = null
) {
    val titleBarColor = if (active) Win98Colors.TitleBar else Win98Colors.TitleBarInactive

    Row(
        modifier = modifier
            .fillMaxWidth()
            .win98BgColor(titleBarColor)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(start = 4.dp, top = 2.dp, end = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Win98Colors.TitleBarText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (showCloseButton && onClose != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .win98OutsetBorder(borderWidth = 1.dp)
                    .win98BgColor(Win98Colors.ButtonFace)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\u00D7",
                    color = Win98Colors.WindowText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 11.sp
                )
            }
        }
    }
}

@Composable
fun Win98Card(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .win98OutsetBorder(borderWidth = 1.dp)
            .win98BgColor(Win98Colors.Background)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            content = content
        )
    }
}

@Composable
fun Win98InsetPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .win98InsetBorder(borderWidth = 1.dp)
            .win98BgColor(Win98Colors.Surface)
            .padding(4.dp),
        content = content
    )
}

@Composable
fun Win98TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                color = Win98Colors.WindowText,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .win98InsetBorder(borderWidth = 1.dp),
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Win98Colors.Surface,
                unfocusedContainerColor = Win98Colors.Surface,
                disabledContainerColor = Win98Colors.ButtonFace.copy(alpha = 0.6f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                cursorColor = Win98Colors.WindowText,
                focusedTextColor = Win98Colors.WindowText,
                unfocusedTextColor = Win98Colors.WindowText,
                disabledTextColor = Win98Colors.GrayText
            ),
            shape = RectangleShape
        )
    }
}

@Composable
fun Win98TaskbarButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val bgColor = if (selected) Win98Colors.ButtonFace else Win98Colors.ButtonFace
    val borderModifier = if (selected) {
        Modifier
            .win98InsetBorder(borderWidth = 1.dp)
    } else {
        Modifier
            .win98OutsetBorder(borderWidth = 1.dp)
    }

    Row(
        modifier = modifier
            .then(borderModifier)
            .win98BgColor(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(28.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun Win98Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.clickable(enabled = enabled) {
            onCheckedChange?.invoke(!checked)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .win98InsetBorder(borderWidth = 1.dp)
                .win98BgColor(Win98Colors.Surface),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text(
                    text = "\u2713",
                    color = Win98Colors.WindowText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 11.sp
                )
            }
        }
        if (label != null) {
            label()
        }
    }
}

@Composable
fun Win98StatusBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .win98InsetBorder(borderWidth = 1.dp)
            .win98BgColor(Win98Colors.ButtonFace)
            .padding(vertical = 2.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun Win98StatusBarItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .win98InsetBorder(borderWidth = 1.dp)
            .win98BgColor(Win98Colors.Surface)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun Win98ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(16.dp)
            .win98InsetBorder(borderWidth = 1.dp)
            .win98BgColor(Win98Colors.Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .win98BgColor(Win98Colors.TitleBar)
                .let {
                    if (progress > 0f) it.fillMaxWidth(progress) else it
                }
        )
    }
}