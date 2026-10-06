package com.mirxlab.termboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mirxlab.termboard.data.KeyboardPreferences
import com.mirxlab.termboard.ui.layers.LayerEditorScreen
import com.mirxlab.termboard.ui.layers.LayerManagerScreen
import com.mirxlab.termboard.ui.layouts.StandardLayoutManagerScreen
import com.mirxlab.termboard.ui.layouts.StandardSettingsScreen
import com.mirxlab.termboard.ui.theme.TermboardTheme
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data object SplitSettingsRoute

@Serializable
data object LayerManagerRoute

@Serializable
data object StandardLayoutsRoute

@Serializable
data object StandardSettingsRoute

@Serializable
data class LayerEditorRoute(val layerId: String, val isNew: Boolean)

@Composable
fun TermboardApp() {
    val context = LocalContext.current
    val preferences = remember { KeyboardPreferences(context) }
    val darkTheme by preferences.darkThemeFlow.collectAsState(initial = true)
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()
    val isEditingLayer = currentRoute.contains(LayerEditorRoute::class.qualifiedName.orEmpty())
    val isSettingsSelected = currentRoute.contains(SplitSettingsRoute::class.qualifiedName.orEmpty())
    val isLayersSelected = currentRoute.contains(LayerManagerRoute::class.qualifiedName.orEmpty())
    val isStandardLayoutsSelected = currentRoute.contains(StandardLayoutsRoute::class.qualifiedName.orEmpty())
    val isStandardSettingsSelected = currentRoute.contains(StandardSettingsRoute::class.qualifiedName.orEmpty())

    TermboardTheme(darkTheme = darkTheme) {
        Scaffold(
            bottomBar = {
                if (!isEditingLayer) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = isSettingsSelected,
                            onClick = { navController.navigate(SplitSettingsRoute) { launchSingleTop = true } },
                            icon = { Icon(Icons.Filled.Tune, contentDescription = "Split settings") },
                            label = { Text("Split settings", fontSize = 10.sp, maxLines = 1) }
                        )
                        NavigationBarItem(
                            selected = isLayersSelected,
                            onClick = { navController.navigate(LayerManagerRoute) { launchSingleTop = true } },
                            icon = { Icon(Icons.Filled.Layers, contentDescription = "Layers") },
                            label = { Text("Split layers", fontSize = 10.sp, maxLines = 1) }
                        )
                        NavigationBarItem(
                            selected = isStandardSettingsSelected,
                            onClick = { navController.navigate(StandardSettingsRoute) { launchSingleTop = true } },
                            icon = { Icon(Icons.Filled.Tune, contentDescription = "Standard settings") },
                            label = { Text("Standard settings", fontSize = 10.sp, maxLines = 1) }
                        )
                        NavigationBarItem(
                            selected = isStandardLayoutsSelected,
                            onClick = { navController.navigate(StandardLayoutsRoute) { launchSingleTop = true } },
                            icon = { Icon(Icons.Filled.Keyboard, contentDescription = "Standard layout") },
                            label = { Text("Standard layout", fontSize = 10.sp, maxLines = 1) }
                        )
                    }
                }
            }
        ) { contentPadding ->
            NavHost(
                navController = navController,
                startDestination = SplitSettingsRoute,
                modifier = Modifier.padding(contentPadding)
            ) {
                composable<SplitSettingsRoute> {
                    MainSettingsScreen()
                }
                composable<LayerManagerRoute> {
                    LayerManagerScreen(
                        onAddLayer = {
                            navController.navigate(
                                LayerEditorRoute("custom_${UUID.randomUUID()}", isNew = true)
                            )
                        },
                        onEditLayer = { layerId ->
                            navController.navigate(LayerEditorRoute(layerId, isNew = false))
                        }
                    )
                }
                composable<StandardLayoutsRoute> {
                    StandardLayoutManagerScreen()
                }
                composable<StandardSettingsRoute> {
                    StandardSettingsScreen()
                }
                composable<LayerEditorRoute> { entry ->
                    val route = entry.toRoute<LayerEditorRoute>()
                    LayerEditorScreen(
                        layerId = route.layerId,
                        isNew = route.isNew,
                        onCancel = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}