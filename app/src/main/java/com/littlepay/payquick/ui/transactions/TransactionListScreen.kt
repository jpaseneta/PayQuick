package com.littlepay.payquick.ui.transactions

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.littlepay.payquick.domain.model.Transaction
import com.littlepay.payquick.ui.theme.PayQuickTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) {
        showLogoutConfirmation = true
    }

    LaunchedEffect(uiState) {
        if (uiState is TransactionUiState.AuthError) {
            Toast.makeText(
                context,
                (uiState as TransactionUiState.AuthError).message,
                Toast.LENGTH_LONG
            ).show()
            showLogoutConfirmation = false
            viewModel.logout()
            onLogout()
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout and exit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("No")
                }
            }
        )
    }

    TransactionListContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        isLoadingMore = isLoadingMore,
        onRefresh = { viewModel.loadTransactions(viewModel.getAuthToken(), isRefresh = true) },
        onConfirmLogout = { showLogoutConfirmation = true },
        onLoadMore = { viewModel.loadTransactions(viewModel.getAuthToken()) },
        onRetry = { viewModel.loadTransactions(viewModel.getAuthToken(), isRefresh = true) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    uiState: TransactionUiState,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    onRefresh: () -> Unit,
    onConfirmLogout: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onConfirmLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is TransactionUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .semantics { contentDescription = "Loading transactions" }
                        )
                    }

                    is TransactionUiState.Success -> {
                        val transactions = uiState.transactions
                        if (transactions.isEmpty()) {
                            Text(
                                text = "No transactions found",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(transactions) { index, transaction ->
                                    TransactionItem(transaction)

                                    if (index == transactions.lastIndex) {
                                        LaunchedEffect(Unit) {
                                            onLoadMore() //load more mechanism
                                        }
                                    }
                                }

                                if (isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .semantics { contentDescription = "Loading more transactions" },
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is TransactionUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = onRetry) {
                                Text("Retry")
                            }
                        }
                    }

                    is TransactionUiState.AuthError -> {
                        // Handled by LaunchedEffect in TransactionListScreen
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val amountText = "${transaction.currency} ${
        String.format(
            Locale.getDefault(),
            "%.2f",
            transaction.amountInCents / 100.0
        )
    }"
    val accessibilityLabel = "Transaction: ${transaction.description}, Date: ${transaction.date}, Amount: $amountText"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityLabel
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleLarge,
                color = if (transaction.amountInCents < 0) Color.Red else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionListPreview() {
    val mockTransactions = listOf(
        Transaction("1", 5000, "USD", "Grocery Store", "2023-10-01", "completed"),
        Transaction("2", -1200, "USD", "Coffee Shop", "2023-10-02", "completed"),
        Transaction("3", 25000, "USD", "Salary", "2023-10-03", "completed")
    )
    PayQuickTheme {
        TransactionListContent(
            uiState = TransactionUiState.Success(mockTransactions),
            isRefreshing = false,
            isLoadingMore = false,
            onRefresh = {},
            onConfirmLogout = {},
            onLoadMore = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    PayQuickTheme {
        TransactionItem(
            transaction = Transaction("1", 5000, "USD", "Grocery Store", "2023-10-01", "completed")
        )
    }
}
