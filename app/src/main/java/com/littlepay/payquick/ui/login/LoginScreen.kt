package com.littlepay.payquick.ui.login

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.littlepay.payquick.R
import com.littlepay.payquick.ui.theme.PayQuickTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    
    val uiState by viewModel.loginState.collectAsState()

    LoginContent(
        modifier = modifier,
        uiState = uiState,
        onLoginClick = { email, password -> viewModel.login(email, password) },
        onLoginSuccess = onLoginSuccess
    )
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Littlepay Logo",
            modifier = Modifier
                .height(49.dp)
                .padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            enabled = uiState !is LoginUiState.Loading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is LoginUiState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        if (uiState is LoginUiState.Error) {
            Text(
                text = (uiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        LaunchedEffect(uiState) {
            if (uiState is LoginUiState.Success) {
                onLoginSuccess((uiState as LoginUiState.Success).token)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PayQuickTheme {
        LoginContent(
            uiState = LoginUiState.Idle,
            onLoginClick = { _, _ -> },
            onLoginSuccess = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingPreview() {
    PayQuickTheme {
        LoginContent(
            uiState = LoginUiState.Loading,
            onLoginClick = { _, _ -> },
            onLoginSuccess = {}
        )
    }
}
