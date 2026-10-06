package com.mirxlab.termboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirxlab.termboard.data.KeyboardPreferences
import com.mirxlab.termboard.data.models.KeyboardMode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TermboardApp()
        }
    }
}

@Composable
fun MainSettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { KeyboardPreferences(context) }

    val opacity by prefs.opacityFlow.collectAsState(initial = 0.85f)
    val splitModeAuto by prefs.splitModeAutoFlow.collectAsState(initial = true)
    val portraitCenterGapRatio by prefs.portraitCenterGapRatioFlow.collectAsState(initial = 1.1f)
    val landscapeCenterGapRatio by prefs.landscapeCenterGapRatioFlow.collectAsState(initial = 0.75f)
    val splitKeyboardHeightDp by prefs.splitKeyboardHeightDpFlow.collectAsState(initial = 300)
    val modifierHoldDurationMs by prefs.modifierHoldDurationMsFlow.collectAsState(initial = 260)
    val darkTheme by prefs.darkThemeFlow.collectAsState(initial = true)

    var testInputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Termboard IME",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Programmable Split Mechanical Tablet Keyboard (QMK / Sofle Inspired)",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // IME Setup Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Keyboard Setup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("1. Enable Termboard in Settings")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.showInputMethodPicker()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("2. Select Termboard as Active IME")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferences Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Keyboard Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Opacity Slider
                Text(
                    text = "Keycap Opacity: ${(opacity * 100).toInt()}%",
                    fontSize = 14.sp
                )
                Slider(
                    value = opacity,
                    onValueChange = { scope.launch { prefs.setOpacity(it) } },
                    valueRange = 0.2f..1.0f
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Portrait Center Gap Ratio Slider
                Text(
                    text = "Portrait split gap: ${(portraitCenterGapRatio / (2f + portraitCenterGapRatio) * 100).toInt()}%",
                    fontSize = 14.sp
                )
                Slider(
                    value = portraitCenterGapRatio,
                    onValueChange = { scope.launch { prefs.setPortraitCenterGapRatio(it) } },
                    valueRange = 0.15f..1.5f
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Landscape split gap: ${(landscapeCenterGapRatio / (2f + landscapeCenterGapRatio) * 100).toInt()}%",
                    fontSize = 14.sp
                )
                Slider(
                    value = landscapeCenterGapRatio,
                    onValueChange = { scope.launch { prefs.setLandscapeCenterGapRatio(it) } },
                    valueRange = 0.15f..1.5f
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Split keyboard height: ${splitKeyboardHeightDp}dp",
                    fontSize = 14.sp
                )
                Slider(
                    value = splitKeyboardHeightDp.toFloat(),
                    onValueChange = { scope.launch { prefs.setSplitKeyboardHeightDp(it.toInt()) } },
                    valueRange = 240f..360f,
                    steps = 11
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Modifier hold activates: ${modifierHoldDurationMs}ms",
                    fontSize = 14.sp
                )
                Slider(
                    value = modifierHoldDurationMs.toFloat(),
                    onValueChange = { scope.launch { prefs.setModifierHoldDurationMs(it.toInt()) } },
                    valueRange = 120f..500f,
                    steps = 18
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dark Theme Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Mechanical Theme",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { scope.launch { prefs.setDarkTheme(it) } }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto Split on Tablet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auto Split in Landscape (>600dp)",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = splitModeAuto,
                        onCheckedChange = { scope.launch { prefs.setSplitModeAuto(it) } }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test Input Field Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Type Tester",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = testInputText,
                    onValueChange = { testInputText = it },
                    label = { Text("Tap here to test Termboard split keyboard...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
