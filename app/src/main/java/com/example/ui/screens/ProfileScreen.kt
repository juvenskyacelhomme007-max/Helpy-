package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.components.GoogleSignInButton
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.HelpyGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.HelpyViewModel

@Composable
fun ProfileScreen(
    viewModel: HelpyViewModel,
    onBack: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    val displayName = currentUser?.displayName ?: "Juvensky Acel'homme"
    val email = currentUser?.email ?: "@juvensky27"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Cityscape Header with Avatar Overlay matching Screen 8
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Sunset city header photo
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.img_city_night)
                    .crossfade(true)
                    .build(),
                contentDescription = "Photo de couverture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            // Top icons: back arrow & 3 dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                IconButton(onClick = { /* Menu */ }, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                }
            }

            // Circular Avatar overlapping bottom center matching Screen 8
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(currentUser?.photoUrl ?: R.drawable.img_user_avatar)
                        .crossfade(true)
                        .error(R.drawable.img_user_avatar)
                        .placeholder(R.drawable.img_user_avatar)
                        .build(),
                    contentDescription = displayName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Camera Edit Badge Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Changer la photo",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Name & Verification Badge matching Screen 8
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Vérifié",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (email.startsWith("@")) email else "@juvensky27",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Achte • Vann • Pataje • Devlope",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row matching Screen 8: 12 Anons | 48 Abonnés | 32 Abonnements
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatItem(count = "12", label = "Anons")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderSubtle))
                ProfileStatItem(count = "48", label = "Abonnés")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderSubtle))
                ProfileStatItem(count = "32", label = "Abonnements")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Modifier le profil" Button matching Screen 8
            OutlinedButton(
                onClick = { /* Edit profile */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(42.dp)
                    .testTag("edit_profile_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = TextDark
                )
            ) {
                Text("Modifier le profil", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Menu Items matching Screen 8
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderSubtle),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ProfileMenuRowItem(
                    icon = Icons.Default.ReceiptLong,
                    title = "Mes annonces",
                    onClick = { /* User ads */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)
                ProfileMenuRowItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "Mes messages",
                    onClick = onNavigateToMessages
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)
                ProfileMenuRowItem(
                    icon = Icons.Default.FavoriteBorder,
                    title = "Mes favoris",
                    onClick = onNavigateToFavorites
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)
                ProfileMenuRowItem(
                    icon = Icons.Default.Settings,
                    title = "Paramètres",
                    onClick = { /* Settings */ }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)
                ProfileMenuRowItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Aide & Support",
                    onClick = { /* Help */ }
                )
            }
        }

        // Google Sign-in if disconnected or Logout if connected
        if (currentUser == null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                GoogleSignInButton(
                    isLoading = isAuthLoading,
                    onSignInClicked = {
                        if (context is Activity) {
                            viewModel.signInWithGoogle(context)
                        }
                    }
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("profile_logout_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Se déconnecter", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontWeight = FontWeight.Black,
            fontSize = 17.sp,
            color = TextDark
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun ProfileMenuRowItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TextDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
