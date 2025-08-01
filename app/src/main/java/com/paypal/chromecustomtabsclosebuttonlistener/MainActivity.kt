package com.paypal.chromecustomtabsclosebuttonlistener

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.paypal.chromecustomtabsclosebuttonlistener.ui.theme.ChromeCustomTabsCloseButtonListenerTheme
import com.paypal.chromecustomtabsclosebuttonlistener.utils.OnLifecycleOwnerResumeEffect
import com.paypal.chromecustomtabsclosebuttonlistener.utils.OnNewIntentEffect
import com.paypal.chromecustomtabsclosebuttonlistener.utils.getActivityOrNull

// Inspiration Ref: https://stackoverflow.com/a/41444238

sealed class ChromeCustomTabsResult {
    data object SuccessViaLifecycleResumed : ChromeCustomTabsResult()
    data object SuccessViaNewIntent : ChromeCustomTabsResult() data object CanceledViaActivityResult : ChromeCustomTabsResult()
    data class Unknown(val resultCode: Int, val intent: Intent?) : ChromeCustomTabsResult()
}

// Ref: https://developer.android.com/training/basics/intents/result#custom
class LaunchChromeCustomTab : ActivityResultContract<Uri, ChromeCustomTabsResult>() {
    override fun createIntent(context: Context, input: Uri): Intent {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val intent = customTabsIntent.intent
        intent.data = input
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ChromeCustomTabsResult {
        return when (resultCode) {
            Activity.RESULT_CANCELED -> ChromeCustomTabsResult.CanceledViaActivityResult
            else -> ChromeCustomTabsResult.Unknown(resultCode, intent)
        }
    }
}

private fun isSuccessUri(uri: Uri?): Boolean {
    return uri?.toString()?.contains("success") ?: false
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChromeCustomTabsCloseButtonListenerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChromeCustomTabsDemo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ChromeCustomTabsDemo(modifier: Modifier = Modifier) {
    var status by remember { mutableStateOf<ChromeCustomTabsResult?>(null) }

    // Ref: https://stackoverflow.com/a/67156998
    val chromeCustomTabsLauncher =
        rememberLauncherForActivityResult(LaunchChromeCustomTab()) { result ->
            // forward result from activity launcher
            status = result
        }

    OnNewIntentEffect { intent ->
        if (isSuccessUri(intent.data)) {
            status = ChromeCustomTabsResult.SuccessViaNewIntent
        }
    }

    val context = LocalContext.current
    OnLifecycleOwnerResumeEffect {
        val intent = context.getActivityOrNull()?.intent
        if (isSuccessUri(intent?.data)) {
            status = ChromeCustomTabsResult.SuccessViaLifecycleResumed
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chrome Custom Tabs Demo")
        Button(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp),
            onClick = {
                val uri = "https://sshropshire.github.io/chrome-custom-tabs-close-button-listener"
                    .toUri()
                    .buildUpon()
                    .appendQueryParameter("callback", "chrome-custom-tabs-demo://success")
                    .build()
                chromeCustomTabsLauncher.launch(uri)
            }) {
            Text(text = "Launch Chrome Custom Tab")
        }

        val message = status?.let { status ->
            when (status) {
                ChromeCustomTabsResult.SuccessViaNewIntent ->
                    """
                    Success!
                    via New Intent
                    """

                ChromeCustomTabsResult.SuccessViaLifecycleResumed ->
                    """
                    Success!
                    via Lifecycle Resumed 
                    """

                ChromeCustomTabsResult.CanceledViaActivityResult ->
                    """
                    User Canceled
                    via Activity Result
                    """

                is ChromeCustomTabsResult.Unknown ->
                    """
                    Unexpected activity result from Chrome Custom Tab
                    ResultCode Code: ${status.resultCode}
                    Intent: ${status.intent}
                    """

            }
        }
        message?.let {
            Text(
                text = it.trimIndent(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChromeCustomTabsCloseButtonListenerTheme {
        ChromeCustomTabsDemo()
    }
}