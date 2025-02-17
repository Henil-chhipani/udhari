package com.example.udhari.ui.about

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compose.AppTheme
import com.example.udhari.navigation.GlobalNavController
import com.example.udhari.ui.commonCoponents.AppIcon
import com.example.udhari.ui.commonCoponents.BackBtn
import com.example.udhari.ui.commonCoponents.TopBar

@Composable
fun AboutScreen() {
    AboutScreenUi()
}

// video id 5X0QFZvolsg?si=_uHtaLkPbKOSEyeC
@Composable
fun AboutScreenUi() {
    var globalNavController = GlobalNavController.current
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopBar(title = {
                AppIcon()
            },
                icon = {
                    BackBtn()
                },
                onIconClick = {
                    globalNavController.popBackStack()
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    item {
                        Text("About", style = MaterialTheme.typography.titleLarge)

                        Text(
                            "Take control of your financial life with our innovative personal finance management app. Track debts, loans, and transactions securely with voice-enabled commands.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        Text(
                            "Video about app",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    item {
                        YouTubeVideo(
                            src = "https://www.youtube.com/embed/5X0QFZvolsg?si=zOEGjJevSN7AgESs",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f) // Keeps the video in 16:9 ratio
                        )
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                        Text("About the Developer", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))

                        Text(
                            "Hey, I'm Henil Chhipani – a passionate Android developer crafting innovative apps like Udhari. Always exploring new technologies and open to collaborations. Let’s connect!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )

                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "🔗 LinkedIn",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable { uriHandler.openUri("https://www.linkedin.com/in/henil-chhipani/") }
                                    .padding(vertical = 6.dp)
                            )
                            Text(
                                text = "🐦 X (Twitter)",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable { uriHandler.openUri("https://x.com/henil_chhipani") }
                                    .padding(vertical = 6.dp)
                            )
                            Text(
                                text = "✉️ Email Me",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:henilchhipani@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Contact via Udhari App")
                                    }
                                    context.startActivity(intent)
                                }.padding(vertical = 6.dp)
                            )
                        }
                    }

                }
            }
        }
    )
}

@Composable
fun YouTubeVideo(src: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            WebView(context).apply {
                // Enable JavaScript for YouTube
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                // Load the YouTube video via an HTML iframe
                loadData(
                    """
                    <html>
                      <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                html, body {
                                    margin: 0;
                                    padding: 0;
                                    height: 100%;
                                    
                                }
                                iframe {
                                    border: 0;
                                    width: 100%;
                                    height: 190;
                                }
                            </style>
                        </head>
                        <body >
                            <iframe  src=$src title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
                        </body>
                    </html>
                    """.trimIndent(),
                    "text/html",
                    "utf-8"
                )
            }
        },
        modifier = modifier
    )
}


@Composable
@Preview(showBackground = true)
fun AboutScreenUiPreview() {
    AppTheme {
        AboutScreenUi()
    }
}