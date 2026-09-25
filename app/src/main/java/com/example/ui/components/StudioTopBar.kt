package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudioTopBar(
    savedCount: Int,
    onOpenLibrary: () -> Unit,
    onOpenInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo Icon
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    brush = Brush.linearGradient(listOf(NeonViolet, RomanticRose)),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Taruni AI",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Taruni",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = NeonViolet.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "AI STUDIO",
                        color = NeonViolet,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "2D/3D Video • Songs • Couple Montage",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onOpenInfo,
            modifier = Modifier.testTag("app_info_button")
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "About Taruni",
                tint = TextSecondary
            )
        }

        IconButton(
            onClick = onOpenLibrary,
            modifier = Modifier.testTag("saved_library_button")
        ) {
            BadgedBox(
                badge = {
                    if (savedCount > 0) {
                        Badge(containerColor = RomanticRose) {
                            Text(text = "$savedCount", color = Color.White)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FolderSpecial,
                    contentDescription = "Library",
                    tint = CyberCyan
                )
            }
        }
    }
}
