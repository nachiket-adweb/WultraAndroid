package com.android.wultrausage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.wultrausage.screens.HucScreen
import com.android.wultrausage.screens.MainScreen
import com.android.wultrausage.screens.OkHttpScreen
import com.android.wultrausage.screens.RetrofitScreen
import com.android.wultrausage.ui.theme.WultraUsageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WultraUsageTheme {
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = "main"
                ) {
                    composable("main") { MainScreen(navController) }
                    composable("huc") { HucScreen(navController) }
                    composable("retrofit") { RetrofitScreen(navController) }
                    composable("okhttp") { OkHttpScreen(navController) }
                }
            }
        }
    }
}
