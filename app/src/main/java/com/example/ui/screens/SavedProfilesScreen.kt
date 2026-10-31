package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensitivityProfile
import com.example.ui.components.TacticalHeader
import com.example.ui.theme.*

@Composable
fun SavedProfilesScreen(
    profiles: List<SensitivityProfile>,
    onDeleteProfile: (SensitivityProfile) -> Unit,
    onRenameProfile: (SensitivityProfile, String) -> Unit,
    onRegenerateSensitivity: () -> Unit
) {
    val context = LocalContext.current
    var profileToRename by remember { mutableStateOf<SensitivityProfile?>(null) }
    var renameText by remember { mutableStateOf("") }
    var selectedForCompare by remember { mutableStateOf<List<SensitivityProfile>>(emptyList()) }
    var showCompareDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp)
            .testTag("saved_profiles_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TacticalHeader(
                title = "SAVED PROFILES",
                subtitle = "${profiles.size} configurations stored locally in SQLite"
            )
            if (profiles.size >= 2) {
                OutlinedButton(
                    onClick = {
                        selectedForCompare = profiles.take(2)
                        showCompareDialog = true
                    },
                    border = BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_compare_profiles")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = "Compare",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("COMPARE", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (profiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("empty_profiles_view"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Empty profiles",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No saved sensitivity presets yet.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Generate your personalized sensitivity and tap 'Save Profile'.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = onRegenerateSensitivity,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_empty_generate")
                    ) {
                        Text(
                            text = "GENERATE SENSITIVITY",
                            color = CyberBlack,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.testTag("profiles_list")
            ) {
                items(profiles, key = { it.id }) { profile ->
                    ProfileCard(
                        profile = profile,
                        onDelete = { onDeleteProfile(profile) },
                        onRename = {
                            profileToRename = profile
                            renameText = profile.name
                        },
                        onShare = { shareSingleProfile(context, profile) }
                    )
                }
            }
        }

        // Rename Dialog
        if (profileToRename != null) {
            AlertDialog(
                onDismissRequest = { profileToRename = null },
                containerColor = DarkSurfaceElevated,
                title = {
                    Text(
                        text = "Rename Profile",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    OutlinedTextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        label = { Text("Profile Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_rename_profile"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (renameText.isNotBlank()) {
                                onRenameProfile(profileToRename!!, renameText.trim())
                                profileToRename = null
                            }
                        },
                        modifier = Modifier.testTag("btn_confirm_rename")
                    ) {
                        Text("SAVE", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { profileToRename = null }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }

        // Compare Profiles Dialog
        if (showCompareDialog && selectedForCompare.size >= 2) {
            CompareProfilesDialog(
                profileA = selectedForCompare[0],
                profileB = selectedForCompare[1],
                onDismiss = { showCompareDialog = false }
            )
        }
    }
}

@Composable
private fun ProfileCard(
    profile: SensitivityProfile,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_card_${profile.id}")
            .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        color = NeonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${profile.deviceModel} • ${profile.playStyle}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Row {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.testTag("btn_share_${profile.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share profile",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onRename,
                        modifier = Modifier.testTag("btn_rename_${profile.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename profile",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("btn_delete_${profile.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete profile",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gen: ${profile.general}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("RedDot: ${profile.redDot}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("2X: ${profile.scope2x}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("4X: ${profile.scope4x}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Btn: ${profile.fireButtonSize}%", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CompareProfilesDialog(
    profileA: SensitivityProfile,
    profileB: SensitivityProfile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        title = {
            Text(
                text = "SIDE-BY-SIDE PROFILE COMPARISON",
                color = NeonCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Parameter", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(profileA.name.take(10), color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(profileB.name.take(10), color = NeonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = BorderSubtle)
                CompareRow("General Drag", profileA.general, profileB.general)
                CompareRow("Red Dot", profileA.redDot, profileB.redDot)
                CompareRow("2X Scope", profileA.scope2x, profileB.scope2x)
                CompareRow("4X Scope", profileA.scope4x, profileB.scope4x)
                CompareRow("Sniper Scope", profileA.sniperScope, profileB.sniperScope)
                CompareRow("Free Look", profileA.freeLook, profileB.freeLook)
                CompareRow("Fire Button", profileA.fireButtonSize, profileB.fireButtonSize, unit = "%")
                CompareRow("Phone DPI", profileA.recommendedDpi, profileB.recommendedDpi)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun CompareRow(name: String, valA: Int, valB: Int, unit: String = "") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = TextPrimary, fontSize = 12.sp)
        Text("$valA$unit", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("$valB$unit", color = NeonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

private fun shareSingleProfile(context: Context, profile: SensitivityProfile) {
    val message = "🎮 Sensitivity: Gen ${profile.general}, RedDot ${profile.redDot}, 2X ${profile.scope2x}, 4X ${profile.scope4x}, AWM ${profile.sniperScope}, Button ${profile.fireButtonSize}% | Configured for ${profile.deviceModel}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "Share Profile"))
}
