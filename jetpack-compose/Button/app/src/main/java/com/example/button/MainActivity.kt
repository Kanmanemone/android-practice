package com.example.button

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.button.ui.theme.ButtonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ButtonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 60.dp)

                    ) {
                        FilledButtonExample("Filled Button")
                        DisabledButtonExample("Disabled Button")
                        FilledTonalButtonExample("Filled Tonal Button")
                        OutlinedButtonExample("Outlined Button")
                        ElevatedButtonExample("Elevated Button")
                        TextButtonExample1("Text Button1")
                        TextButtonExample2("Text Button2")
                        IconButtonExample(Icons.Filled.Refresh)
                    }
                }
            }
        }
    }
}

@Composable
fun FilledButtonExample(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    Button(
        onClick = { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam
    ) {
        Text(text = textParam)
    }
}

@Composable
fun DisabledButtonExample(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    Button(
        onClick = { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam,
        enabled = false
    ) {
        Text(text = textParam)
    }
}

@Composable
fun FilledTonalButtonExample(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    FilledTonalButton(
        onClick = { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam
    ) {
        Text(text = textParam)
    }
}

@Composable
fun OutlinedButtonExample(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam
    ) {
        Text(text = textParam)
    }
}

@Composable
fun ElevatedButtonExample(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    ElevatedButton(
        onClick = { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam
    ) {
        Text(text = textParam)
    }
}

@Composable
fun TextButtonExample1(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    TextButton(
        onClick = { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam
    ) {
        Text(text = textParam)
    }
}

@Composable
fun TextButtonExample2(textParam: String, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    Text(
        modifier = modifierParam.clickable { Toast.makeText(context, "Clicked on $textParam", Toast.LENGTH_SHORT).show() },
        text = textParam
    )
}

@Composable
fun IconButtonExample(imageVectorParam: ImageVector, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    IconButton(
        onClick = { Toast.makeText(context, "Clicked on ${imageVectorParam.name}", Toast.LENGTH_SHORT).show() },
        modifier = modifierParam
    ) {
        Icon(
            imageVectorParam,
            contentDescription = imageVectorParam.name,
            tint = Color.DarkGray,
            modifier = Modifier.size(40.dp)
        )
    }
}