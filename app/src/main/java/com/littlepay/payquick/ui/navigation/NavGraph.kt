package com.littlepay.payquick.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.littlepay.payquick.ui.login.LoginScreen
import com.littlepay.payquick.ui.login.LoginViewModel
import com.littlepay.payquick.ui.transactions.TransactionListScreen
import com.littlepay.payquick.ui.transactions.TransactionViewModel

@Composable
fun PayQuickNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("transactions/") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
        composable(
            route = "transactions/",
        ) {
            val viewModel: TransactionViewModel = hiltViewModel()
            TransactionListScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("transactions/") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
