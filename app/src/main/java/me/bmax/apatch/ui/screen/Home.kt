package me.bmax.apatch.ui.screen

import android.os.Build
import android.system.Os
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.InstallMobile
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.dropUnlessResumed
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.InstallModeSelectScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PatchesDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.bmax.apatch.APApplication
import me.bmax.apatch.Natives
import me.bmax.apatch.R
import me.bmax.apatch.apApp
import me.bmax.apatch.ui.component.ProvideMenuShape
import me.bmax.apatch.ui.component.WarningCard
import me.bmax.apatch.ui.component.rememberConfirmDialog
import me.bmax.apatch.ui.theme.Win98Button
import me.bmax.apatch.ui.theme.Win98Colors
import me.bmax.apatch.ui.theme.Win98TitleBar
import me.bmax.apatch.ui.theme.win98Divider
import me.bmax.apatch.ui.theme.win98InsetBorder
import me.bmax.apatch.ui.theme.win98OutsetBorder
import me.bmax.apatch.ui.viewmodel.PatchesViewModel
import me.bmax.apatch.util.LatestVersionInfo
import me.bmax.apatch.util.Version
import me.bmax.apatch.util.Version.getManagerVersion
import me.bmax.apatch.util.checkNewVersion
import me.bmax.apatch.util.getSELinuxStatus
import me.bmax.apatch.util.reboot
import me.bmax.apatch.util.ui.APDialogBlurBehindUtils

private val managerVersion = getManagerVersion()

private fun Modifier.win98Bg(color: Color): Modifier = this.drawBehind { drawRect(color) }

@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(navigator: DestinationsNavigator) {
    val kpState by APApplication.kpStateLiveData.observeAsState(APApplication.State.UNKNOWN_STATE)
    val apState by APApplication.apStateLiveData.observeAsState(APApplication.State.UNKNOWN_STATE)

    Scaffold(topBar = {
        Win98HomeTopBar(onInstallClick = dropUnlessResumed {
            navigator.navigate(InstallModeSelectScreenDestination)
        }, navigator, kpState)
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
                .win98Bg(Win98Colors.Background),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            WarningCard()
            KStatusCard(kpState, apState, navigator)
            if (kpState != APApplication.State.UNKNOWN_STATE && apState != APApplication.State.ANDROIDPATCH_INSTALLED) {
                AStatusCard(apState)
            }
            val checkUpdate = APApplication.sharedPreferences.getBoolean("check_update", true)
            if (checkUpdate) {
                UpdateCard()
            }
            InfoCard(kpState, apState)
            LearnMoreCard()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun UninstallDialog(showDialog: MutableState<Boolean>, navigator: DestinationsNavigator) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { showDialog.value = false },
        properties = DialogProperties(
            decorFitsSystemWindows = true,
            usePlatformDefaultWidth = false,
        ),
        confirmButton = {
            Win98Button(onClick = {
                showDialog.value = false
                APApplication.uninstallApatch()
                navigator.navigate(PatchesDestination(PatchesViewModel.PatchMode.UNPATCH))
            }) {
                Text(stringResource(id = R.string.home_dialog_uninstall_all))
            }
        },
        dismissButton = {
            Win98Button(onClick = {
                showDialog.value = false
                APApplication.uninstallApatch()
            }) {
                Text(stringResource(id = R.string.home_dialog_uninstall_ap_only))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.home_dialog_uninstall_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        shape = RoundedCornerShape(0.dp),
        containerColor = Win98Colors.Background,
        titleContentColor = Win98Colors.WindowText
    )
    val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
    APDialogBlurBehindUtils.setupWindowBlurListener(dialogWindowProvider.window)
}

@Composable
fun RebootDropdownItem(@StringRes id: Int, reason: String = "") {
    DropdownMenuItem(text = {
        Text(stringResource(id), fontSize = 12.sp)
    }, onClick = {
        reboot(reason)
    })
}

@Composable
private fun Win98HomeTopBar(
    onInstallClick: () -> Unit, navigator: DestinationsNavigator, kpState: APApplication.State
) {
    val uriHandler = LocalUriHandler.current
    var showDropdownMoreOptions by remember { mutableStateOf(false) }
    var showDropdownReboot by remember { mutableStateOf(false) }

    Column(modifier = Modifier.win98Bg(Win98Colors.TitleBar)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = Win98Colors.TitleBarText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onInstallClick) {
                Icon(
                    imageVector = Icons.Filled.InstallMobile,
                    contentDescription = stringResource(id = R.string.mode_select_page_title),
                    tint = Win98Colors.TitleBarText,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (kpState != APApplication.State.UNKNOWN_STATE) {
                IconButton(onClick = {
                    showDropdownReboot = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(id = R.string.reboot),
                        tint = Win98Colors.TitleBarText,
                        modifier = Modifier.size(18.dp)
                    )
                    ProvideMenuShape(RoundedCornerShape(0.dp)) {
                        DropdownMenu(expanded = showDropdownReboot, onDismissRequest = {
                            showDropdownReboot = false
                        }) {
                            RebootDropdownItem(id = R.string.reboot)
                            RebootDropdownItem(id = R.string.reboot_recovery, reason = "recovery")
                            RebootDropdownItem(id = R.string.reboot_bootloader, reason = "bootloader")
                            RebootDropdownItem(id = R.string.reboot_download, reason = "download")
                            RebootDropdownItem(id = R.string.reboot_edl, reason = "edl")
                        }
                    }
                }
            }
            Box {
                IconButton(onClick = { showDropdownMoreOptions = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.settings),
                        tint = Win98Colors.TitleBarText,
                        modifier = Modifier.size(18.dp)
                    )
ProvideMenuShape(RoundedCornerShape(0.dp)) {
                    DropdownMenu(expanded = showDropdownMoreOptions, onDismissRequest = {
                        showDropdownMoreOptions = false
                    }) {
                        DropdownMenuItem(text = {
                            Text(stringResource(R.string.home_more_menu_feedback_or_suggestion), fontSize = 12.sp)
                            }, onClick = {
                                showDropdownMoreOptions = false
                                uriHandler.openUri("https://github.com/bmax121/APatch/issues/new/choose")
                            })
                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.home_more_menu_about), fontSize = 12.sp)
                            }, onClick = {
                                navigator.navigate(AboutScreenDestination)
                                showDropdownMoreOptions = false
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KStatusCard(
    kpState: APApplication.State, apState: APApplication.State, navigator: DestinationsNavigator
) {
    val showUninstallDialog = remember { mutableStateOf(false) }
    if (showUninstallDialog.value) {
        UninstallDialog(showDialog = showUninstallDialog, navigator)
    }

    val cardColor = when (kpState) {
        APApplication.State.KERNELPATCH_INSTALLED -> Win98Colors.TitleBar
        APApplication.State.KERNELPATCH_NEED_UPDATE, APApplication.State.KERNELPATCH_NEED_REBOOT -> Win98Colors.ButtonShadow
        else -> Win98Colors.Desktop
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .win98OutsetBorder(borderWidth = 1.dp)
            .win98Bg(Win98Colors.Background)
    ) {
        Win98TitleBar(
            title = stringResource(R.string.kernel_patch),
            active = kpState == APApplication.State.KERNELPATCH_INSTALLED
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (kpState) {
                    APApplication.State.KERNELPATCH_INSTALLED -> {
                        Icon(Icons.Filled.CheckCircle, stringResource(R.string.home_working), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                    APApplication.State.KERNELPATCH_NEED_UPDATE, APApplication.State.KERNELPATCH_NEED_REBOOT -> {
                        Icon(Icons.Outlined.SystemUpdate, stringResource(R.string.home_need_update), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                    else -> {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, "Unknown", tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                }
                Column(
                    Modifier
                        .weight(2f)
                        .padding(start = 12.dp, end = 4.dp)
                ) {
                    when (kpState) {
                        APApplication.State.KERNELPATCH_INSTALLED -> {
                            Text(
                                text = stringResource(R.string.home_working),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                        }
                        APApplication.State.KERNELPATCH_NEED_UPDATE, APApplication.State.KERNELPATCH_NEED_REBOOT -> {
                            Text(
                                text = stringResource(R.string.home_need_update),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.kpatch_version_update,
                                    Version.installedKPVString(),
                                    Version.buildKPVString()
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Win98Colors.WindowText
                            )
                        }
                        else -> {
                            Text(
                                text = stringResource(R.string.home_install_unknown),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                            Text(
                                text = stringResource(R.string.home_install_unknown_summary),
                                style = MaterialTheme.typography.bodySmall,
                                color = Win98Colors.WindowText
                            )
                        }
                    }
                    if (kpState != APApplication.State.UNKNOWN_STATE && kpState != APApplication.State.KERNELPATCH_NEED_UPDATE && kpState != APApplication.State.KERNELPATCH_NEED_REBOOT) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${Version.installedKPVString()} (${managerVersion.second}) - " + if (apState != APApplication.State.ANDROIDPATCH_NOT_INSTALLED) "Full" else "KernelPatch",
                            style = MaterialTheme.typography.bodySmall,
                            color = Win98Colors.WindowText
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Win98Button(onClick = {
                        when (kpState) {
                            APApplication.State.UNKNOWN_STATE -> {
                                navigator.navigate(InstallModeSelectScreenDestination)
                            }
                            APApplication.State.KERNELPATCH_NEED_UPDATE -> {
                                if (Version.installedKPVUInt() < 0x900u) {
                                    navigator.navigate(PatchesDestination(PatchesViewModel.PatchMode.PATCH_ONLY))
                                } else {
                                    navigator.navigate(InstallModeSelectScreenDestination)
                                }
                            }
                            APApplication.State.KERNELPATCH_NEED_REBOOT -> {
                                reboot()
                            }
                            APApplication.State.KERNELPATCH_UNINSTALLING -> {
                            }
                            else -> {
                                if (apState == APApplication.State.ANDROIDPATCH_INSTALLED || apState == APApplication.State.ANDROIDPATCH_NEED_UPDATE) {
                                    showUninstallDialog.value = true
                                } else {
                                    navigator.navigate(PatchesDestination(PatchesViewModel.PatchMode.UNPATCH))
                                }
                            }
                        }
                    }) {
                        when (kpState) {
                            APApplication.State.UNKNOWN_STATE -> {
                                Text(text = stringResource(id = R.string.home_ap_cando_install), fontSize = 12.sp)
                            }
                            APApplication.State.KERNELPATCH_NEED_UPDATE -> {
                                Text(text = stringResource(id = R.string.home_ap_cando_update), fontSize = 12.sp)
                            }
                            APApplication.State.KERNELPATCH_NEED_REBOOT -> {
                                Text(text = stringResource(id = R.string.home_ap_cando_reboot), fontSize = 12.sp)
                            }
                            APApplication.State.KERNELPATCH_UNINSTALLING -> {
                                Icon(Icons.Outlined.Cached, contentDescription = "busy", modifier = Modifier.size(14.dp))
                            }
                            else -> {
                                Text(text = stringResource(id = R.string.home_ap_cando_uninstall), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AStatusCard(apState: APApplication.State) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .win98OutsetBorder(borderWidth = 1.dp)
            .win98Bg(Win98Colors.Background)
    ) {
        Win98TitleBar(
            title = stringResource(R.string.android_patch)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (apState) {
                    APApplication.State.ANDROIDPATCH_NOT_INSTALLED -> {
                        Icon(Icons.Outlined.Block, stringResource(R.string.home_not_installed), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                    APApplication.State.ANDROIDPATCH_INSTALLING -> {
                        Icon(Icons.Outlined.InstallMobile, stringResource(R.string.home_installing), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                    APApplication.State.ANDROIDPATCH_INSTALLED -> {
                        Icon(Icons.Outlined.CheckCircle, stringResource(R.string.home_working), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                    APApplication.State.ANDROIDPATCH_NEED_UPDATE -> {
                        Icon(Icons.Outlined.SystemUpdate, stringResource(R.string.home_need_update), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                    else -> {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, stringResource(R.string.home_install_unknown), tint = Win98Colors.WindowText, modifier = Modifier.size(20.dp))
                    }
                }
                Column(
                    Modifier
                        .weight(2f)
                        .padding(start = 12.dp)
                ) {
                    when (apState) {
                        APApplication.State.ANDROIDPATCH_NOT_INSTALLED -> {
                            Text(
                                text = stringResource(R.string.home_not_installed),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                        }
                        APApplication.State.ANDROIDPATCH_INSTALLING -> {
                            Text(
                                text = stringResource(R.string.home_installing),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                        }
                        APApplication.State.ANDROIDPATCH_INSTALLED -> {
                            Text(
                                text = stringResource(R.string.home_working),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                        }
                        APApplication.State.ANDROIDPATCH_NEED_UPDATE -> {
                            Text(
                                text = stringResource(R.string.home_need_update),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.apatch_version_update,
                                    Version.installedApdVString,
                                    managerVersion.second
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Win98Colors.WindowText
                            )
                        }
                        else -> {
                            Text(
                                text = stringResource(R.string.home_install_unknown),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Win98Colors.WindowText
                            )
                        }
                    }
                }
                if (apState != APApplication.State.UNKNOWN_STATE) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Win98Button(onClick = {
                            when (apState) {
                                APApplication.State.ANDROIDPATCH_NOT_INSTALLED, APApplication.State.ANDROIDPATCH_NEED_UPDATE -> {
                                    APApplication.installApatch()
                                }
                                APApplication.State.ANDROIDPATCH_UNINSTALLING -> {
                                }
                                else -> {
                                    APApplication.uninstallApatch()
                                }
                            }
                        }) {
                            when (apState) {
                                APApplication.State.ANDROIDPATCH_NOT_INSTALLED -> {
                                    Text(text = stringResource(id = R.string.home_ap_cando_install), fontSize = 12.sp)
                                }
                                APApplication.State.ANDROIDPATCH_NEED_UPDATE -> {
                                    Text(text = stringResource(id = R.string.home_ap_cando_update), fontSize = 12.sp)
                                }
                                APApplication.State.ANDROIDPATCH_UNINSTALLING -> {
                                    Icon(Icons.Outlined.Cached, contentDescription = "busy", modifier = Modifier.size(14.dp))
                                }
                                else -> {
                                    Text(text = stringResource(id = R.string.home_ap_cando_uninstall), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WarningCard() {
    var show by rememberSaveable { mutableStateOf(apApp.getBackupWarningState()) }
    if (show) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .win98OutsetBorder(borderWidth = 1.dp)
                .win98Bg(Win98Colors.ErrorBackground.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "warning", tint = Win98Colors.WindowText, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.patch_warnning),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Win98Colors.WindowText,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Outlined.Clear,
                    contentDescription = "",
                    tint = Win98Colors.WindowText,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            show = false
                            apApp.updateBackupWarningState(false)
                        }
                )
            }
        }
    }
}

private fun getSystemVersion(): String {
    return "${Build.VERSION.RELEASE} ${if (Build.VERSION.PREVIEW_SDK_INT != 0) "Preview" else ""} (API ${Build.VERSION.SDK_INT})"
}

private fun getDeviceInfo(): String {
    var manufacturer =
        Build.MANUFACTURER[0].uppercaseChar().toString() + Build.MANUFACTURER.substring(1)
    if (!Build.BRAND.equals(Build.MANUFACTURER, ignoreCase = true)) {
        manufacturer += " " + Build.BRAND[0].uppercaseChar() + Build.BRAND.substring(1)
    }
    manufacturer += " " + Build.MODEL + " "
    return manufacturer
}

@Composable
private fun InfoCard(kpState: APApplication.State, apState: APApplication.State) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .win98OutsetBorder(borderWidth = 1.dp)
            .win98Bg(Win98Colors.Background)
    ) {
        Win98TitleBar(title = "Info")
        Column(
            modifier = Modifier
                .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
                .win98InsetBorder(borderWidth = 1.dp)
                .padding(4.dp)
        ) {
            val uname = Os.uname()

            @Composable
            fun InfoCardItem(label: String, content: String) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Win98Colors.WindowText)
                Text(text = content, style = MaterialTheme.typography.bodySmall, color = Win98Colors.WindowText)
            }

            if (kpState != APApplication.State.UNKNOWN_STATE) {
                InfoCardItem(
                    stringResource(R.string.home_kpatch_version), Version.installedKPVString()
                )
                Spacer(Modifier.height(8.dp))
                InfoCardItem(stringResource(R.string.home_su_path), Natives.suPath())
                Spacer(Modifier.height(8.dp))
            }

            if (apState != APApplication.State.UNKNOWN_STATE && apState != APApplication.State.ANDROIDPATCH_NOT_INSTALLED) {
                InfoCardItem(
                    stringResource(R.string.home_apatch_version), managerVersion.second.toString()
                )
                Spacer(Modifier.height(8.dp))
            }

            InfoCardItem(stringResource(R.string.home_device_info), getDeviceInfo())
            Spacer(Modifier.height(8.dp))
            InfoCardItem(stringResource(R.string.home_kernel), uname.release)
            Spacer(Modifier.height(8.dp))
            InfoCardItem(stringResource(R.string.home_system_version), getSystemVersion())
            Spacer(Modifier.height(8.dp))
            InfoCardItem(stringResource(R.string.home_fingerprint), Build.FINGERPRINT)
            Spacer(Modifier.height(8.dp))
            InfoCardItem(stringResource(R.string.home_selinux_status), getSELinuxStatus())
        }
    }
}

@Composable
fun UpdateCard() {
    val latestVersionInfo = LatestVersionInfo()
    val newVersion by produceState(initialValue = latestVersionInfo) {
        value = withContext(Dispatchers.IO) {
            checkNewVersion()
        }
    }
    val currentVersionCode = managerVersion.second
    val newVersionCode = newVersion.versionCode
    val newVersionUrl = newVersion.downloadUrl
    val changelog = newVersion.changelog

    val uriHandler = LocalUriHandler.current
    val title = stringResource(id = R.string.apm_changelog)
    val updateText = stringResource(id = R.string.apm_update)

    AnimatedVisibility(
        visible = newVersionCode > currentVersionCode,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val updateDialog = rememberConfirmDialog(onConfirm = { uriHandler.openUri(newVersionUrl) })
        WarningCard(
            message = stringResource(id = R.string.home_new_apatch_found).format(newVersionCode),
            Win98Colors.GrayText
        ) {
            if (changelog.isEmpty()) {
                uriHandler.openUri(newVersionUrl)
            } else {
                updateDialog.showConfirm(
                    title = title, content = changelog, markdown = true, confirm = updateText
                )
            }
        }
    }
}

@Composable
fun LearnMoreCard() {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .win98OutsetBorder(borderWidth = 1.dp)
            .win98Bg(Win98Colors.Background)
            .clickable {
                uriHandler.openUri("https://apatch.dev")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_learn_apatch),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Win98Colors.LinkColor
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.home_click_to_learn_apatch),
                    style = MaterialTheme.typography.bodySmall,
                    color = Win98Colors.WindowText
                )
            }
        }
    }
}