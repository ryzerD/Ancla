package co.ryzer.ancla.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnclaTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigationClick: (() -> Unit)? = null,
    navigationContentDescription: String? = null,
    centerTitle: Boolean = false,
    windowInsets: WindowInsets = WindowInsets(0)
) {
    val navigationIcon: @Composable (() -> Unit) = {
        if (onNavigationClick != null) {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = navigationContentDescription,
                    tint = TextPrimary
                )
            }
        }
    }

    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = AnclaBackground,
        titleContentColor = TextPrimary,
        navigationIconContentColor = TextPrimary
    )

    if (centerTitle) {
        CenterAlignedTopAppBar(
            modifier = modifier.fillMaxWidth(),
            title = {
                Text(
                    text = title,
                    style = AnclaTextStyles.sectionLabel,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            },
            navigationIcon = navigationIcon,
            windowInsets = windowInsets,
            colors = colors
        )
    } else {
        TopAppBar(
            modifier = modifier.fillMaxWidth(),
            title = {
                Text(
                    text = title,
                    style = AnclaTextStyles.sectionLabel,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            },
            navigationIcon = navigationIcon,
            windowInsets = windowInsets,
            colors = colors
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnclaTopBarPreview() {
    AnclaTheme {
        AnclaTopBar(
            title = "Ancla Top Bar",
            onNavigationClick = {},
            navigationContentDescription = "Back"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnclaTopBarCenteredPreview() {
    AnclaTheme {
        AnclaTopBar(
            title = "Centered Title",
            centerTitle = true,
            onNavigationClick = {},
            navigationContentDescription = "Back"
        )
    }
}

