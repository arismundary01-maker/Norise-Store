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

private val DarkColorScheme = darkColorScheme(
  primary = DarkRosePrimary,
  onPrimary = DarkRoseOnPrimary,
  primaryContainer = DarkRosePrimaryContainer,
  onPrimaryContainer = DarkRoseOnPrimaryContainer,
  secondary = ChampagneSecondary,
  onSecondary = ChampagneOnSecondary,
  background = DarkBackground,
  surface = DarkSurface,
  surfaceVariant = DarkSurfaceVariant,
  onBackground = DarkOnSurface,
  onSurface = DarkOnSurface,
  onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
  primary = RosePrimary,
  onPrimary = RoseOnPrimary,
  primaryContainer = RosePrimaryContainer,
  onPrimaryContainer = RoseOnPrimaryContainer,
  secondary = ChampagneSecondary,
  onSecondary = ChampagneOnSecondary,
  secondaryContainer = ChampagneSecondaryContainer,
  onSecondaryContainer = ChampagneOnSecondaryContainer,
  tertiary = SageTertiary,
  onTertiary = SageOnTertiary,
  tertiaryContainer = SageTertiaryContainer,
  onTertiaryContainer = SageOnTertiaryContainer,
  background = WarmBackground,
  surface = WarmSurface,
  surfaceVariant = WarmSurfaceVariant,
  onBackground = WarmOnSurface,
  onSurface = WarmOnSurface,
  onSurfaceVariant = WarmOnSurfaceVariant,
  outline = WarmOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors by default for consistent boutique look
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
