package com.example.lazycolumn

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.lazycolumn.ui.theme.LazyColumnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val list = ArrayList<String>()
                    for (i: Int in 1..20) {
                        list.add("$i")
                    }
                    val modifier = Modifier.fillMaxSize()
                    LazyColumnExample(list, modifier)
                }
            }
        }
    }
}

@Composable
fun LazyColumnExample(itemsParam: List<String>, modifierParam: Modifier = Modifier) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifierParam

    ) {
        items(itemsParam) {
            val modifier = Modifier.clickable {
                Toast.makeText(context, "Clicked item $it", Toast.LENGTH_SHORT).show()
            }
            TextExample("$it", modifier)
        }
    }
}

@Composable
fun TextExample(
    name: String,
    modifierParam: Modifier = Modifier,
    fontSizeParam: TextUnit = 40.sp
) {
    Text(
        text = "$name",
        modifier = modifierParam,
        fontSize = fontSizeParam,
        textAlign = TextAlign.Center
    )
}