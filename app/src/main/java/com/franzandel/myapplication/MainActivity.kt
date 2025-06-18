package com.franzandel.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.franzandel.myapplication.api.Post
import com.franzandel.myapplication.network.NetworkManager
import com.franzandel.myapplication.ui.theme.DependencyLockingSampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val networkManager = NetworkManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DependencyLockingSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DependencyLockingScreen(networkManager, lifecycleScope)
                }
            }
        }
    }
}

@Composable
fun DependencyLockingScreen(
    networkManager: NetworkManager,
    lifecycleScope: CoroutineScope
) {
    var post by remember { mutableStateOf<Post?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DependencyLockingScreenContent(
        isLoading = isLoading,
        post = post,
        errorMessage = errorMessage,
        onApiCallClick = {
            isLoading = true
            errorMessage = null
            post = null

            lifecycleScope.launch {
                networkManager.getRandomPost().fold(
                    onSuccess = { result ->
                        post = result
                        isLoading = false
                    },
                    onFailure = { exception ->
                        errorMessage = exception.message ?: "Unknown error occurred"
                        isLoading = false
                    }
                )
            }
        }
    )
}

@Composable
fun DependencyLockingScreenContent(
    isLoading: Boolean,
    post: Post?,
    errorMessage: String?,
    onApiCallClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Dependency Locking Showcase",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "🔍 Version Conflict Check",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• OkHttp Declared: 3.3.1 (ancient, 2016)\n" +
                            "• Retrofit Version: 2.8.1 (using OkHttp 3.14.7)\n" +
                            "• OkHttp Resolved: 3.14.7 (by Gradle)\n" +
                            "• Logging Interceptor: 3.3.1 (old APIs)\n" +
                            "• Result: ❌ NoClassDefFoundError at runtime\n" +
                            "• Full Error : java.lang.NoClassDefFoundError: Failed resolution of: Lokhttp3/internal/http/HttpEngine;\n\n" +
                            "• Solution :\n" +
                            "1. Implement Dependency Locking to elevate such issues.\n" +
                            "2. Use compatible versions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Button(
            onClick = onApiCallClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Loading...")
                }
            } else {
                // Will trigger below error :
                // java.lang.NoClassDefFoundError: Failed resolution of: Lokhttp3/internal/http/HttpEngine;
                Text("🚀 Make API Call")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display result
        when {
            post != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Post ID: ${post.id}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = post.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DependencyLockingScreenPreview() {
    DependencyLockingSampleTheme {
        DependencyLockingScreenContent(
            isLoading = false,
            post = null,
            errorMessage = null,
            onApiCallClick = { }
        )
    }
}