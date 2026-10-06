package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.LarkMusicApp
import com.example.viewmodel.LarkMusicViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LarkMusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LarkMusicApp(viewModel = viewModel)
        }
    }
}

