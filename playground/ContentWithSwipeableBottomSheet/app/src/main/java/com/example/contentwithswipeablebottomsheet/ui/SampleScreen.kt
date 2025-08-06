package com.example.contentwithswipeablebottomsheet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.contentwithswipeablebottomsheet.R
import com.example.contentwithswipeablebottomsheet.component.BottomSheetBackHandler
import com.example.contentwithswipeablebottomsheet.component.ImeSheetExclusivityHandler
import com.example.contentwithswipeablebottomsheet.component.TextFieldAutoFocusOnceWithoutIme
import com.example.contentwithswipeablebottomsheet.component.core.BottomOccupier
import com.example.contentwithswipeablebottomsheet.component.core.ContentWithSwipeableBottomSheet
import com.example.contentwithswipeablebottomsheet.component.core.SheetValue
import com.example.contentwithswipeablebottomsheet.component.core.rememberContentWithSwipeableBottomSheetState
import com.example.contentwithswipeablebottomsheet.data.storedImeMaxHeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleScreen() {
    val scope = rememberCoroutineScope()

    val sheetState = rememberContentWithSwipeableBottomSheetState()
    ImeSheetExclusivityHandler(sheetState.anchoredDraggableState)

    var sheetExpandedAnchorOffset by remember { mutableStateOf(0.dp) }

    ContentWithSwipeableBottomSheet(
        sheetContent = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(30) {
                    item {
                        Text("${it + 1}")
                    }
                }

                item {
                    Spacer(Modifier.navigationBarsPadding())
                }
            }

            BottomSheetBackHandler(sheetState.anchoredDraggableState)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(BottomAppBarDefaults.containerColor),
        sheetState = sheetState,
        sheetExpandedAnchorOffset = sheetExpandedAnchorOffset,
        sheetPartiallyExpandedHeight = storedImeMaxHeight(),
        sheetSwipeEnabled = true,
        hasContentNavigationBarPadding = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("제목")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    sheetState.expand()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_double_arrow_up_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                                contentDescription = "Bottom Sheet Expand"
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    when (sheetState.currentValue) {
                                        SheetValue.Hidden -> sheetState.partiallyExpand()
                                        SheetValue.PartiallyExpanded -> sheetState.expand()
                                        SheetValue.Expanded -> {}
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Bottom Sheet Up"
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    when (sheetState.currentValue) {
                                        SheetValue.Hidden -> {}
                                        SheetValue.PartiallyExpanded -> sheetState.hide()
                                        SheetValue.Expanded -> sheetState.partiallyExpand()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Bottom Sheet Down"
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.keyboard_double_arrow_down_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                                contentDescription = "Bottom Sheet Hide"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = BottomAppBarDefaults.containerColor
                ) {
                    val imeController = LocalSoftwareKeyboardController.current
                    IconButton(
                        onClick = {
                            scope.launch {
                                when (sheetState.bottomOccupier) {
                                    BottomOccupier.None, BottomOccupier.Ime -> sheetState.partiallyExpand()
                                    BottomOccupier.BottomSheet -> imeController?.show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = when (sheetState.bottomOccupier) {
                                BottomOccupier.None, BottomOccupier.Ime ->
                                    ImageVector.vectorResource(id = R.drawable.bottom_drawer_24dp_1f1f1f_fill0_wght400_grad0_opsz24)

                                BottomOccupier.BottomSheet ->
                                    ImageVector.vectorResource(id = R.drawable.keyboard_24dp_1f1f1f_fill0_wght400_grad0_opsz24)
                            },
                            contentDescription = ""
                        )
                    }

                    val focusRequester = remember { FocusRequester() }
                    val interactionSource = remember { MutableInteractionSource() }
                    TextFieldAutoFocusOnceWithoutIme(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        interactionSource = interactionSource
                    )
                }
            },
        ) { innerPadding ->
            LaunchedEffect(innerPadding) {
                sheetExpandedAnchorOffset = innerPadding.calculateTopPadding()
            }

            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
                    reverseLayout = true
                ) {
                    repeat(30) {
                        item {
                            Text("${it + 1}")
                        }
                    }
                }
            }
        }
    }
}