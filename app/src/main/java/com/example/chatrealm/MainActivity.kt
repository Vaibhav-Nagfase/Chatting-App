package com.example.chatrealm

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatrealm.ViewModel.AuthViewModel
import com.example.chatrealm.screen.ChatBotScreen
import com.example.chatrealm.screen.ChatRoomListScreen
import com.example.chatrealm.screen.ChatScreen
import com.example.chatrealm.screen.LoginScreen
import com.example.chatrealm.screen.SignUpScreen
import com.example.chatrealm.ui.theme.ChatRealmTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel:AuthViewModel = viewModel()
            ChatRealmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(authViewModel = authViewModel,navController = navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.SignUpScreen.route
    ) {
        composable(Screen.SignUpScreen.route){
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {navController.navigate(Screen.LoginScreen.route)}
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {navController.navigate(Screen.SignUpScreen.route)}
            ){
                navController.navigate(Screen.ChatRoomsScreen.route)
            }
        }

        composable(Screen.ChatRoomsScreen.route) {
            ChatRoomListScreen(
                onJoinClicked =  {
                    navController.navigate("${Screen.ChatScreen.route}/${it.id}")
                }
            ){
                navController.navigate(Screen.ChatBotScreen.route)
            }
        }

        composable("${Screen.ChatScreen.route}/{roomId}") {
            val roomId: String = it
                .arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }

        composable(Screen.ChatBotScreen.route) {
            ChatBotScreen()
        }


    }
}
