package me.bmax.apatch.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.ImageLoader
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.isRouteOnBackStackAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import me.bmax.apatch.APApplication
import me.bmax.apatch.R
import me.bmax.apatch.ui.screen.BottomBarDestination
import me.bmax.apatch.ui.theme.APatchTheme
import me.bmax.apatch.ui.theme.Win98Colors
import me.bmax.apatch.ui.theme.Win98TaskbarButton
import me.bmax.apatch.ui.theme.win98Divider
import me.bmax.apatch.ui.theme.win98OutsetBorder
import me.bmax.apatch.ui.viewmodel.SuperUserViewModel
import me.bmax.apatch.util.ui.LocalSnackbarHost
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer

class MainActivity : AppCompatActivity() {

    private var isLoading = true

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().setKeepOnScreenCondition { isLoading }

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)

        setContent {
            APatchTheme {
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                val configuration = LocalConfiguration.current
                val bottomBarRoutes = remember {
                    BottomBarDestination.entries.map { it.direction.route }.toSet()
                }
                val state by APApplication.apStateLiveData.observeAsState(APApplication.State.UNKNOWN_STATE)
                val kPatchReady = state != APApplication.State.UNKNOWN_STATE
                val aPatchReady = state == APApplication.State.ANDROIDPATCH_INSTALLED
                val visibleDestinations = remember(state) {
                    BottomBarDestination.entries.filter { destination ->
                        !(destination.kPatchRequired && !kPatchReady) && !(destination.aPatchRequired && !aPatchReady)
                    }.toSet()
                }

                val defaultTransitions = object : NavHostAnimatedDestinationStyle() {
                    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                        {
                            if (targetState.destination.route !in bottomBarRoutes) {
                                slideInHorizontally(initialOffsetX = { it })
                            } else {
                                fadeIn(animationSpec = tween(340))
                            }
                        }

                    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                        {
                            if (initialState.destination.route in bottomBarRoutes && targetState.destination.route !in bottomBarRoutes) {
                                slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()
                            } else {
                                fadeOut(animationSpec = tween(340))
                            }
                        }

                    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                        {
                            if (targetState.destination.route in bottomBarRoutes) {
                                slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn()
                            } else {
                                fadeIn(animationSpec = tween(340))
                            }
                        }

                    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                        {
                            if (initialState.destination.route !in bottomBarRoutes) {
                                scaleOut(targetScale = 0.9f) + fadeOut()
                            } else {
                                fadeOut(animationSpec = tween(340))
                            }
                        }
                }

                LaunchedEffect(Unit) {
                    if (SuperUserViewModel.apps.isEmpty()) {
                        SuperUserViewModel().fetchAppList()
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            Win98Taskbar(navController, visibleDestinations)
                        }
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    CompositionLocalProvider(
                        LocalSnackbarHost provides snackBarHostState,
                    ) {
                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            Row(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))) {
                                Win98SideNav(
                                    navController = navController,
                                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                                    visibleDestinations = visibleDestinations
                                )
                                DestinationsNavHost(
                                    modifier = Modifier
                                        .weight(1f)
                                        .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start)),
                                    navGraph = NavGraphs.root,
                                    navController = navController,
                                    engine = rememberNavHostEngine(navHostContentAlignment = Alignment.TopCenter),
                                    defaultTransitions = defaultTransitions
                                )
                            }
                        } else {
                            DestinationsNavHost(
                                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                                navGraph = NavGraphs.root,
                                navController = navController,
                                engine = rememberNavHostEngine(navHostContentAlignment = Alignment.TopCenter),
                                defaultTransitions = defaultTransitions
                            )
                        }
                    }
                }
            }
        }

        val iconSize = resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    add(AppIconKeyer())
                    add(AppIconFetcher.Factory(iconSize, false, this@MainActivity))
                }
                .build()
        )

        isLoading = false
    }
}

@Composable
private fun Win98Taskbar(navController: NavHostController, visibleDestinations: Set<BottomBarDestination>) {
    val navigator = navController.rememberDestinationsNavigator()

    Column(modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
    ) {
        Row(
            modifier = Modifier
                .win98Divider()
                .fillMaxWidth()
                .background(Win98Colors.Background)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            visibleDestinations.forEach { destination ->
                val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(destination.direction)

                Win98TaskbarButton(
                    selected = isCurrentDestOnBackStack,
                    onClick = {
                        if (isCurrentDestOnBackStack) {
                            navigator.popBackStack(destination.direction, false)
                        }
                        navigator.navigate(destination.direction) {
                            popUpTo(NavGraphs.root) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (isCurrentDestOnBackStack) destination.iconSelected else destination.iconNotSelected,
                        stringResource(destination.label),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(destination.label),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        softWrap = false,
                        fontSize = 11.sp,
                        fontWeight = if (isCurrentDestOnBackStack) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun Win98SideNav(navController: NavHostController, modifier: Modifier = Modifier, visibleDestinations: Set<BottomBarDestination>) {
    val navigator = navController.rememberDestinationsNavigator()

    Column(
        modifier = modifier
            .background(Win98Colors.Background)
            .win98OutsetBorder(borderWidth = 1.dp)
    ) {
        visibleDestinations.forEach { destination ->
            val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(destination.direction)
            Win98TaskbarButton(
                selected = isCurrentDestOnBackStack,
                onClick = {
                    if (isCurrentDestOnBackStack) {
                        navigator.popBackStack(destination.direction, false)
                    }
                    navigator.navigate(destination.direction) {
                        popUpTo(NavGraphs.root) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    if (isCurrentDestOnBackStack) destination.iconSelected else destination.iconNotSelected,
                    stringResource(destination.label),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(destination.label),
                    fontSize = 11.sp,
                    fontWeight = if (isCurrentDestOnBackStack) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

private fun Modifier.background(color: Color): Modifier = this.drawBehind {
    drawRect(color)
}

@Composable
private fun Spacer(modifier: Modifier) {
    Box(modifier = modifier)
}