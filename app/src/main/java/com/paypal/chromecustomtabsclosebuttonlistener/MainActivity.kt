package com.paypal.chromecustomtabsclosebuttonlistener

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.paypal.chromecustomtabsclosebuttonlistener.ui.theme.ChromeCustomTabsCloseButtonListenerTheme

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
    val context = LocalContext.current
    Column(
        modifier = modifier
    ) {
        Text(text = "Hello $name!")
        Button(onClick = {
            val url = "https://sshropshire.github.io/chrome-custom-tabs-close-button-listener/"
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context, url.toUri())
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