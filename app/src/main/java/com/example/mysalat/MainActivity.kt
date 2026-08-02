package com.example.mysalat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mysalat.ui.navigation.AppScaffold
import com.example.mysalat.ui.theme.MySalatTheme

/**
 * Single activity host. All UI lives in `ui/`; this class only wires the theme
 * and the navigation shell.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySalatTheme {
                AppScaffold(onExit = { finish() })
            }
        }
    }
}
