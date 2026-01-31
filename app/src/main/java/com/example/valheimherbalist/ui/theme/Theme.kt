package com.example.valheimherbalist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HearthAmber,
    onPrimary = Color.Black,

    primaryContainer = Color(0xFF3A2A14),      // dark amber-brown
    onPrimaryContainer = BoneWhite,

    secondary = MossGreen,
    onSecondary = BoneWhite,

    secondaryContainer = Color(0xFF243128),    // deep moss
    onSecondaryContainer = BoneWhite,

    tertiary = RuneTeal,
    onTertiary = BoneWhite,

    tertiaryContainer = Color(0xFF223133),     // deep teal slate
    onTertiaryContainer = BoneWhite,

    background = CharcoalBlack,
    onBackground = BoneWhite,

    surface = DarkWood,
    onSurface = BoneWhite,

    surfaceVariant = StoneGray,
    onSurfaceVariant = AshGray,

    outline = Color(0xFF5A544B),
    error = Color(0xFFB85C5C),
    onError = Color(0xFF1A0C0C)
)

private val LightColorScheme = lightColorScheme(
    primary = HearthAmber,
    secondary = MossGreen,
    tertiary = RuneTeal,

    background = Color(0xFFF3EFE6), // parchment
    surface = Color(0xFFFAF7F1),

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    onBackground = Color(0xFF2B2A26),
    onSurface = Color(0xFF2B2A26)
)

@Composable
fun ValheimHerbalistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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