package com.boutaina.todo.user

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage

class UserActivity : ComponentActivity() {

    private var showPermissionDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userViewModel = remember { UserViewModel() }
            UserScreen(userViewModel)
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { granted ->
                    if (granted) {
                        pickPhotoWithPermission()
                    } else {
                        showPermissionDialog = true
                    }
                }
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Button to pick photo
                Button(
                    onClick = {
                        val permissionStatus = ContextCompat.checkSelfPermission(
                            this@UserActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )

                        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
                        val isExplanationNeeded = ActivityCompat.shouldShowRequestPermissionRationale(
                            this@UserActivity, Manifest.permission.READ_EXTERNAL_STORAGE
                        )

                        when {
                            isAlreadyAccepted -> {
                                pickPhotoWithPermission()
                            }
                            isExplanationNeeded -> {
                                showPermissionDialog = true
                            }
                            else -> {
                                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Photo")
                }

                if (showPermissionDialog) {
                    PermissionExplanationDialog(
                        onDismiss = { showPermissionDialog = false },
                        onRequestPermission = { requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                    )
                }
            }
        }
    }

    private fun pickPhotoWithPermission() {
        // Handle the photo picking logic here
        Toast.makeText(this, "Permission granted, you can pick a photo now.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun PermissionExplanationDialog(onDismiss: () -> Unit, onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = { Text("We need permission to access your storage to pick a photo.") },
        confirmButton = {
            TextButton(
                onClick = {
                    onRequestPermission()
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UserScreen(userViewModel: UserViewModel) {
    val userName by userViewModel.userName.collectAsState()
    val userAvatarUri by userViewModel.userAvatarUri.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bonjour, $userName", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        userAvatarUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Avatar de l'utilisateur",
                modifier = Modifier.size(100.dp)
            )
        } ?: Text("Aucun avatar disponible")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { userViewModel.fetchUser() }) {
            Text("Rafraîchir les infos")
        }
    }
}

