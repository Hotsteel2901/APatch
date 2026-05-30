package me.bmax.apatch.ui.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.dropUnlessResumed
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.PatchesDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import me.bmax.apatch.R
import me.bmax.apatch.ui.component.WarningCard
import me.bmax.apatch.ui.component.rememberConfirmDialog
import me.bmax.apatch.ui.theme.Win98Colors
import me.bmax.apatch.ui.viewmodel.PatchesViewModel
import me.bmax.apatch.util.isABDevice
import me.bmax.apatch.util.rootAvailable

var selectedBootImage: Uri? = null

@Destination<RootGraph>
@Composable
fun InstallModeSelectScreen(navigator: DestinationsNavigator) {
    var installMethod by remember {
        mutableStateOf<InstallMethod?>(null)
    }

    Scaffold(topBar = {
        TopBar(
            onBack = dropUnlessResumed { navigator.popBackStack() },
        )
    }) {
        Column(modifier = Modifier.padding(it).drawBehind { drawRect(Win98Colors.Background) }) {
            SelectInstallMethod(
                onSelected = { method ->
                    installMethod = method
                },
                navigator = navigator
            )

        }
    }
}

sealed class InstallMethod {
    data class SelectFile(
        val uri: Uri? = null,
        @param:StringRes override val label: Int = R.string.mode_select_page_select_file,
    ) : InstallMethod()

    data object DirectInstall : InstallMethod() {
        override val label: Int
            get() = R.string.mode_select_page_patch_and_install
    }

    data object DirectInstallToInactiveSlot : InstallMethod() {
        override val label: Int
            get() = R.string.mode_select_page_install_inactive_slot
    }

    abstract val label: Int
    open val summary: String? = null
}

@Composable
private fun SelectInstallMethod(
    onSelected: (InstallMethod) -> Unit = {},
    navigator: DestinationsNavigator
) {
    val rootAvailable = rootAvailable()
    val isAbDevice = isABDevice()

    val radioOptions =
        mutableListOf<InstallMethod>(InstallMethod.SelectFile())
    if (rootAvailable) {
        radioOptions.add(InstallMethod.DirectInstall)
        if (isAbDevice) {
            radioOptions.add(InstallMethod.DirectInstallToInactiveSlot)
        }
    }

    var selectedOption by remember { mutableStateOf<InstallMethod?>(null) }
    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { uri ->
                val option = InstallMethod.SelectFile(uri)
                selectedOption = option
                onSelected(option)
                selectedBootImage = option.uri
                navigator.navigate(PatchesDestination(PatchesViewModel.PatchMode.PATCH_ONLY))
            }
        }
    }

    val confirmDialog = rememberConfirmDialog(onConfirm = {
        selectedOption = InstallMethod.DirectInstallToInactiveSlot
        onSelected(InstallMethod.DirectInstallToInactiveSlot)
        navigator.navigate(PatchesDestination(PatchesViewModel.PatchMode.INSTALL_TO_NEXT_SLOT))
    }, onDismiss = null)
    val dialogTitle = stringResource(id = android.R.string.dialog_alert_title)
    val dialogContent = stringResource(id = R.string.mode_select_page_install_inactive_slot_warning)

    val onClick = { option: InstallMethod ->
        when (option) {
            is InstallMethod.SelectFile -> {
                selectedBootImage = null
                selectImageLauncher.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "application/octet-stream"
                    }
                )
            }

            is InstallMethod.DirectInstall -> {
                selectedOption = option
                onSelected(option)
                navigator.navigate(PatchesDestination(PatchesViewModel.PatchMode.PATCH_AND_INSTALL))
            }

            is InstallMethod.DirectInstallToInactiveSlot -> {
                confirmDialog.showConfirm(dialogTitle, dialogContent)
            }
        }
    }

    Column {
        if (!rootAvailable) {
            Box(Modifier.padding(12.dp)) {
                WarningCard(
                    message = stringResource(R.string.home_install_unknown_summary),
                    color = Win98Colors.GrayText,
                )
            }
        }
        radioOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(option)
                    }) {
                RadioButton(selected = option.javaClass == selectedOption?.javaClass, onClick = {
                    onClick(option)
                })
                Column {
                    Text(
                        text = stringResource(id = option.label),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                        fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                        color = Win98Colors.WindowText
                    )
                    option.summary?.let {
                        Text(
                            text = it,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                            fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                            color = Win98Colors.GrayText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit = {}) {
    Column(modifier = Modifier.drawBehind { drawRect(Win98Colors.TitleBar) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.mode_select_page_title),
                color = Win98Colors.TitleBarText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Win98Colors.TitleBarText
                )
            }
        }
    }
}