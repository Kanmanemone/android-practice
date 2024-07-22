package com.example.layout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val modifier = Modifier.fillMaxSize()

            ColumnScrollExample(modifier)
            //RowScrollExample(modifier)

            //ColumnLayoutExample(modifier)
            //RowLayoutExample(modifier)
            //BoxLayoutExample(modifier)
        }
    }
}

@Composable
fun ColumnScrollExample(modifierParams: Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifierParams.verticalScroll(scrollState) // 스크롤 위치를 기억하지 않으려면, verticalScroll(ScrollState(0))

    ) {
        for (i: Int in 1..20) {
            TextExample("$i")
        }
    }
}

@Composable
fun RowScrollExample(modifierParams: Modifier) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifierParams.horizontalScroll(scrollState) // 스크롤 위치를 기억하지 않으려면, verticalScroll(ScrollState(0))

    ) {
        for (i: Int in 1..20) {
            TextExample("$i ")
        }
    }
}

@Composable
fun ColumnLayoutExample(modifierParams: Modifier) {
    Column(
        modifier = modifierParams,

        /* verticalArrangement: Arrangement.Vertical
         * Arrangement.Top (기본값)
         * Arrangement.Center
         * Arrangement.Bottom
         * Arrangement.SpaceEvenly
         * Arrangement.SpaceAround
         * Arrangement.SpaceBetween
         */
        verticalArrangement = Arrangement.Top,

        /* horizontalAlignment: Alignment.Horizontal
         * Alignment.Start (기본값)
         * Alignment.CenterHorizontally
         * Alignment.End
         */
        horizontalAlignment = Alignment.Start

    ) {
        TextExample("1")
        TextExample("2")
        TextExample("3")
        TextExample("4")
        TextExample("5")
    }
}

@Composable
fun RowLayoutExample(modifierParams: Modifier) {
    Row(
        modifier = modifierParams,

        /* horizontalArrangement: Arrangement.Horizontal
         * Arrangement.Start (기본값)
         * Arrangement.Center
         * Arrangement.End
         * Arrangement.SpaceEvenly
         * Arrangement.SpaceAround
         * Arrangement.SpaceBetween
         */
        horizontalArrangement = Arrangement.Start,

        /* verticalAlignment: Alignment.Vertical
         * Alignment.Top (기본값)
         * Alignment.CenterVertically
         * Alignment.Bottom
         */
        verticalAlignment = Alignment.Top

    ) {
        TextExample("1")
        TextExample("2")
        TextExample("3")
        TextExample("4")
        TextExample("5")
    }
}

@Composable
fun BoxLayoutExample(modifierParams: Modifier) {
    Box(
        modifier = modifierParams

    ) {
        val fontSize = 30.sp

        TextExample("Top\nStart", Modifier.align(Alignment.TopStart), fontSize) // (기본값)
        TextExample("Top\nCenter", Modifier.align(Alignment.TopCenter), fontSize)
        TextExample("Top\nEnd", Modifier.align(Alignment.TopEnd), fontSize)
        TextExample("Center\nStart", Modifier.align(Alignment.CenterStart), fontSize)
        TextExample("Center", Modifier.align(Alignment.Center), fontSize)
        TextExample("Center\nEnd", Modifier.align(Alignment.CenterEnd), fontSize)
        TextExample("Bottom\nStart", Modifier.align(Alignment.BottomStart), fontSize)
        TextExample("Bottom\nCenter", Modifier.align(Alignment.BottomCenter), fontSize)
        TextExample("Bottom\nEnd", Modifier.align(Alignment.BottomEnd), fontSize)
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