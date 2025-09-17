package com.example.wifisecure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifisecure.ui.theme.WifiSecureTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold

/*
  Entry point for when the app starts.
  Calls the composable that renders the main page.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WifiSecureTheme {
                MainScreen()
                }
            }
        }
    }

/*
  Composable that renders the main page.
  Renders the top app bar and
  calls other composables that render everything below the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center, text = "WifiSecure"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF27619b),
                    titleContentColor = Color.White
                )
            )
        }
    )
    { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ){
            FoundAndFilter()
            WifiList()
        }
    }
}

// Composable that renders "Found" text and filter icon.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundAndFilter() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween){
        // Hardcoded to 6. Will replace later.
        Text(text = "Found(6)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 13.dp, horizontal = 8.dp))
        // Will implement button logic later.
        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter icon",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

// Composable that renders the list of WiFi cards.
@Composable
fun WifiList() {
            LazyColumn(modifier = Modifier.padding(top = 1.dp).height(620.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),) {
                // Hardcoded to 10. Will replace later.
                items(10) {
                    ElevatedCard(elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp),
                        modifier = Modifier.fillMaxWidth().height(120.dp).
                                    padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0E0E0)
                        )) {
                        Text(text = "Wifi Name",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
                    }
                }
            }
}

// Renders the app in Preview Mode.
@Preview(showBackground = true)
@Composable
fun Preview() {
    WifiSecureTheme {
        MainScreen()
    }
}