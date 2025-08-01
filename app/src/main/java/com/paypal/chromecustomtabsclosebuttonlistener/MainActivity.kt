package com.paypal.chromecustomtabsclosebuttonlistener

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.paypal.chromecustomtabsclosebuttonlistener.ui.theme.ChromeCustomTabsCloseButtonListenerTheme

// Ref: https://stackoverflow.com/a/41444238
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChromeCustomTabsCloseButtonListenerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Ref: https://stackoverflow.com/a/67156998
    val chromeCustomTabsLauncher =
        rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            Log.d("CCT", result.toString())
        }

    Column(
        modifier = modifier
    ) {
        Text(text = "Hello $name!")
        Button(onClick = {
            val uri = "https://sshropshire.github.io/chrome-custom-tabs-close-button-listener"
                .toUri()
                .buildUpon()
                .appendQueryParameter("callback", "chrome-custom-tabs-demo://success")
                .build()
            val customTabsIntent = CustomTabsIntent.Builder().build()
            val intent = customTabsIntent.intent
            intent.data = uri
            chromeCustomTabsLauncher.launch(intent)
        }) {
            Text(text = "Launch Chrome Custom Tab")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChromeCustomTabsCloseButtonListenerTheme {
        Greeting("Android")
    }
}