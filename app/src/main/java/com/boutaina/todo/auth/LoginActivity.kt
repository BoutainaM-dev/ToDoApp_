package com.boutaina.todo.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.boutaina.todo.MainActivity
import com.boutaina.todo.data.Api
import com.boutaina.todo.list.TaskListFragment

class LoginActivity : ComponentActivity() {

    private val codeAuthFlowFactory = AndroidCodeAuthFlowFactory()

    private val oAuthClient = OpenIdConnectClient {
        endpoints {
            authorizationEndpoint = "https://todoist.com/oauth/authorize"
            tokenEndpoint = "https://todoist.com/oauth/access_token"
        }
        clientId = "a9400191c656400fb31a043e530c5522"
        clientSecret = "198815c02ac94f029a00338135a45123"
        scope = "task:add,data:read_write,data:delete"
        redirectUri = "https://cyrilfind.kodo/redirect_uri"
    }

    private val tokenRepository by lazy { TokenRepository(this) }  // Initialize TokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the activity with the OAuth flow factory
        codeAuthFlowFactory.registerActivity(this)

        // Set content view with NavController to handle navigation
        setContent {
            val navController = rememberNavController()

            // Set up navigation and initial screen
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(navController = navController) { login(navController) }
                }
                composable("taskList") {
                    // TaskListFragment or a Composable that displays tasks
                    TaskListFragment()
                }
            }
        }
    }

    private fun login(navController: NavController) {
        lifecycleScope.launch {
            try {
                val codeAuthFlow = codeAuthFlowFactory.createAuthFlow(oAuthClient)
                val result = codeAuthFlow.getAccessToken()

                val token = result.access_token
                tokenRepository.store(token)
                Api.TOKEN = token  // Assigne le token à Api


                // Instead of navigating to taskList directly, we should navigate back to MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()  // Close the login activity to prevent the user from going back to it

            } catch (e: Exception) {
                // Handle errors like network failures
                println("Error during OAuth flow: ${e.message}")
            }
        }
    }

}

@Composable
fun LoginScreen(navController: NavController, onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to the ToDo App", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login with Todoist")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController) {}
}
