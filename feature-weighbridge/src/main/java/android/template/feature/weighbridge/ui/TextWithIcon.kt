package android.template.feature.weighbridge.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    icon: ImageVector,
    iconSize: Dp = 24.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = icon,
            contentDescription = null // Set a content description if needed
        )
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(start = 8.dp) // Add padding between the icon and text
        )
    }
}