package com.littlepay.payquick.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.littlepay.payquick.ui.login.LoginScreen
import com.littlepay.payquick.ui.login.LoginViewModel

@Composable
fun PayQuickNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token ->
                    navController.navigate("transactions/$token") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = loginViewModel
            )
        }
    }
}
