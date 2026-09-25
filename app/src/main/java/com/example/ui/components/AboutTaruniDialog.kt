package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RomanticRose
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardStroke
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutTaruniDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = StudioDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardStroke)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = NeonViolet.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Taruni",
                                    tint = NeonViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("About Taruni", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("AI Creative Suite", color = CyberCyan, fontSize = 11.sp)
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_about_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FeatureItem(
                    icon = Icons.Default.Videocam,
                    iconColor = CyberCyan,
                    title = "1. 2D & 3D Video Studio",
                    desc = "Generate rich multi-scene cinematic videos from text prompts. Choose between 2D Anime/Cartoon and 3D Unreal/Pixar styles with camera motion."
                )

                Spacer(modifier = Modifier.height(12.dp))

                FeatureItem(
                    icon = Icons.Default.MusicNote,
                    iconColor = NeonViolet,
                    title = "2. AI Song & Melody Maker",
                    desc = "Compose full songs with chords, lyrics, and genres. Features a live real-time audio synthesizer engine with reactive visualizer bars!"
                )

                Spacer(modifier = Modifier.height(12.dp))

                FeatureItem(
                    icon = Icons.Default.Favorite,
                    iconColor = RomanticRose,
                    title = "3. Auto-Edit Couple Video & Montage",
                    desc = "Create personalized romantic couple montages with beat-sync transitions, Ken Burns motion, custom names, and love quotes."
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                ) {
                    Text("Start Creating", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            color = iconColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
