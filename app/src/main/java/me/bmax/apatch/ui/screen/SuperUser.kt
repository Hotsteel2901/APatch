package me.bmax.apatch.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch
import me.bmax.apatch.APApplication
import me.bmax.apatch.Natives
import me.bmax.apatch.R
import me.bmax.apatch.apApp
import me.bmax.apatch.ui.component.ProvideMenuShape
import me.bmax.apatch.ui.component.SwitchItem
import me.bmax.apatch.ui.theme.Win98Button
import me.bmax.apatch.ui.theme.Win98Colors
import me.bmax.apatch.ui.theme.Win98TextField
import me.bmax.apatch.ui.theme.Win98TitleBar
import me.bmax.apatch.ui.theme.win98Divider
import me.bmax.apatch.ui.theme.win98InsetBorder
import me.bmax.apatch.ui.theme.win98OutsetBorder
import me.bmax.apatch.ui.viewmodel.SuperUserViewModel
import me.bmax.apatch.util.PkgConfig


@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SuperUserScreen() {
    val viewModel = viewModel<SuperUserViewModel>()
    val scope = rememberCoroutineScope()
    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (viewModel.appList.isEmpty()) {
            viewModel.fetchAppList()
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.drawBehind { drawRect(Win98Colors.TitleBar) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.su_title),
                        color = Win98Colors.TitleBarText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Box {
                        IconButton(onClick = { showDropdown = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(id = R.string.settings),
                                tint = Win98Colors.TitleBarText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        ProvideMenuShape(RoundedCornerShape(0.dp)) {
                            DropdownMenu(expanded = showDropdown, onDismissRequest = {
                                showDropdown = false
                            }) {
                                DropdownMenuItem(text = {
                                    Text(stringResource(R.string.su_refresh), fontSize = 12.sp)
                                }, onClick = {
                                    scope.launch {
                                        viewModel.fetchAppList()
                                    }
                                    showDropdown = false
                                })

                                DropdownMenuItem(text = {
                                    Text(
                                        if (viewModel.showSystemApps) {
                                            stringResource(R.string.su_hide_system_apps)
                                        } else {
                                            stringResource(R.string.su_show_system_apps)
                                        },
                                        fontSize = 12.sp
                                    )
                                }, onClick = {
                                    viewModel.showSystemApps = !viewModel.showSystemApps
                                    showDropdown = false
                                })
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .drawBehind { drawRect(Win98Colors.Background) }
        ) {
            Win98TextField(
                value = viewModel.search,
                onValueChange = { viewModel.search = it },
                label = stringResource(R.string.search_apps),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .win98Divider()
            )
            PullToRefreshBox(
                modifier = Modifier.weight(1f),
                onRefresh = { scope.launch { viewModel.fetchAppList() } },
                isRefreshing = viewModel.isRefreshing
            ) {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp, 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(viewModel.appList.filter { it.packageName != apApp.packageName }, key = { it.packageName + it.uid }) { app ->
                        AppItem(app)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppItem(
    app: SuperUserViewModel.AppInfo,
) {
    val config = app.config
    var showEditProfile by remember { mutableStateOf(false) }
    var rootGranted by remember { mutableStateOf(config.allow != 0) }
    var excludeApp by remember { mutableIntStateOf(config.exclude) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .win98OutsetBorder(borderWidth = 1.dp)
            .drawBehind { drawRect(Win98Colors.Background) }
            .clickable(onClick = {
                if (!rootGranted) {
                    showEditProfile = !showEditProfile
                } else {
                    rootGranted = false
                    config.allow = 0
                    Natives.revokeSu(app.uid)
                    PkgConfig.changeConfig(config)
                }
            })
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(app.packageInfo)
                    .crossfade(true).build(),
                contentDescription = app.label,
                modifier = Modifier
                    .padding(4.dp)
                    .width(48.dp)
                    .height(48.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    app.label,
                    fontWeight = FontWeight.Bold,
                    color = Win98Colors.WindowText,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    app.packageName,
                    color = Win98Colors.GrayText,
                    style = MaterialTheme.typography.bodySmall
                )
                FlowRow {

                    if (excludeApp == 1) {
                        LabelText(label = stringResource(id = R.string.su_pkg_excluded_label))
                    }
                    if (rootGranted) {
                        LabelText(label = config.profile.uid.toString())
                        LabelText(label = config.profile.toUid.toString())
                        LabelText(
                            label = when {
                                config.profile.scontext.isNotEmpty() -> config.profile.scontext
                                else -> stringResource(id = R.string.su_selinux_via_hook)
                            }
                        )
                    }
                }
            }
            Switch(checked = rootGranted, onCheckedChange = {
                rootGranted = !rootGranted
                if (rootGranted) {
                    excludeApp = 0
                    config.allow = 1
                    config.exclude = 0
                    config.profile.scontext = APApplication.MAGISK_SCONTEXT
                } else {
                    config.allow = 0
                }
                config.profile.uid = app.uid
                PkgConfig.changeConfig(config)
                if (config.allow == 1) {
                    Natives.grantSu(app.uid, 0, config.profile.scontext)
                    Natives.setUidExclude(app.uid, 0)
                } else {
                    Natives.revokeSu(app.uid)
                }
            })
        }

        AnimatedVisibility(
            visible = showEditProfile && !rootGranted,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SwitchItem(
                icon = Icons.Filled.Security,
                title = stringResource(id = R.string.su_pkg_excluded_setting_title),
                summary = stringResource(id = R.string.su_pkg_excluded_setting_summary),
                checked = excludeApp == 1,
                onCheckedChange = {
                    if (it) {
                        excludeApp = 1
                        config.allow = 0
                        config.profile.scontext = APApplication.DEFAULT_SCONTEXT
                        Natives.revokeSu(app.uid)
                    } else {
                        excludeApp = 0
                    }
                    config.exclude = excludeApp
                    config.profile.uid = app.uid
                    PkgConfig.changeConfig(config)
                    Natives.setUidExclude(app.uid, excludeApp)
                },
            )
        }
    }
}

@Composable
fun LabelText(label: String) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp, end = 4.dp)
            .background(
                Win98Colors.SelectedBackground, shape = RectangleShape
            )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp),
            style = TextStyle(
                fontSize = 8.sp,
                color = Win98Colors.SelectedText,
            )
        )
    }
}