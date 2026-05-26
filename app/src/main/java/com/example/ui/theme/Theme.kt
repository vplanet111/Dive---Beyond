package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = AquaCyanNeon,
    secondary = MarineTealBright,
    tertiary = GlowGreenClean,
    background = OceanBlueAbyss,
    surface = OceanSurfaceBlue,
    onPrimary = OceanBlueAbyss,
    onSecondary = Color.White,
    onBackground = DeepWhiteText,
    onSurface = DeepWhiteText,
    error = CoralAlertRed,
    surfaceVariant = OceanTealDepth,
    onSurfaceVariant = DeepWhiteText
  )

private val LightColorScheme = DarkColorScheme // Override to enforce the glorious premium ocean night mode by default for high-contrast boat/beach visibility


@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
