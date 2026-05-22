package com.example.shilpakala

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.shilpakala.navigation.AppNavGraph
import com.example.shilpakala.state.StudioViewModel
import com.example.shilpakala.ui.theme.ShilpaKalaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request Camera Permission
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }

        enableEdgeToEdge()

        setContent {

            ShilpaKalaTheme {

                val navController = rememberNavController()

                val studioViewModel: StudioViewModel = viewModel()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { _ ->

                    AppNavGraph(
                        navController = navController,
                        studioViewModel = studioViewModel
                    )
                }
            }
        }
    }
}