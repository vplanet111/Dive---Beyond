package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.DiverViewModel
import com.example.ui.DiverApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val viewModel = DiverViewModel(application)
    
    setContent {
      MyApplicationTheme {
        DiverApp(viewModel = viewModel)
      }
    }
  }
}
