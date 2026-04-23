package com.littlepay.payquick.ui.transactions

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.littlepay.payquick.domain.model.Transaction
import com.littlepay.payquick.ui.theme.PayQuickTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    token: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: TransactionViewModel = if (context is ComponentActivity) {
        viewModel(viewModelStoreOwner = context)
    } else {
        viewModel()
    }
    
    val uiState by viewModel.uiState.collectAsState()

    TransactionListContent(
        uiState = uiState,
        onBack = onBack,
        onLoadMore = { viewModel.loadTransactions(token) },
        onRetry = { viewModel.loadTransactions(token, isRefresh = true) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    uiState: TransactionUiState,
    onBack: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is TransactionUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                                        onLoadMore()
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
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                text = "${transaction.currency} ${String.format(Locale.getDefault(), "%.2f", transaction.amountInCents / 100.0)}",
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
            onBack = {},
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
