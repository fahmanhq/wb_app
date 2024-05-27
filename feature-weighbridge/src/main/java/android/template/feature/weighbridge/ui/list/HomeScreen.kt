/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.feature.weighbridge.ui.list

import android.template.core.data.model.FleetType
import android.template.core.data.model.SortingOption
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.ui.MyApplicationTheme
import android.template.feature.weighbridge.R
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Date

@Composable
fun HomeScreen(
    viewModel: RecordListViewModel = hiltViewModel(),
    onCreateTicketBtnClicked: () -> Unit,
    onViewDetailsBtnClicked: (String) -> Unit,
    onEditBtnClicked: (String) -> Unit
) {
    val items by viewModel.uiState.collectAsState()
    val appliedFilter by viewModel.filterParam.collectAsState()

    if (items is RecordListUiState.Success) {
        HomeScreen(
            recordList = (items as RecordListUiState.Success).data,
            appliedFilter = appliedFilter,
            onCreateTicketBtnClicked = onCreateTicketBtnClicked,
            onViewDetailsBtnClicked = onViewDetailsBtnClicked,
            onEditBtnClicked = onEditBtnClicked,
            onSortOptionSelected = viewModel::onSortOptionSelected
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HomeScreen(
    recordList: List<WeighbridgeRecord>,
    appliedFilter: TicketListSortingParam,
    onCreateTicketBtnClicked: () -> Unit,
    onViewDetailsBtnClicked: (String) -> Unit,
    onEditBtnClicked: (String) -> Unit,
    onSortOptionSelected: (SortingOption, Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }
    var isSortDialogVisible by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopBar(
                onFilterMenuClicked = { isSortDialogVisible = true }
            )
        },
        floatingActionButton = {
            CreateTicketFAB(
                expandedFab = expandedFab,
                onCreateTicketBtnClicked = onCreateTicketBtnClicked
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            state = listState,
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recordList, key = { it.recordId }) { record ->
                RecordCard(
                    modifier = Modifier.animateItemPlacement(),
                    spec = record,
                    onViewDetailsBtnClicked = onViewDetailsBtnClicked,
                    onEditBtnClicked = onEditBtnClicked
                )
            }
        }

        if (isSortDialogVisible) {
            SortingDialog(
                onDismissRequest = { isSortDialogVisible = false },
                appliedFilter = appliedFilter,
                onSortOptionSelected = { sortingOption, isAscending ->
                    onSortOptionSelected(sortingOption, isAscending)
                    isSortDialogVisible = false
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    onFilterMenuClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.weighbridge_ticketing_title),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            IconButton(onClick = onFilterMenuClicked) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = stringResource(R.string.content_desc_filter_menu)
                )
            }
        }
    )
}

@Composable
private fun CreateTicketFAB(
    expandedFab: Boolean,
    onCreateTicketBtnClicked: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onCreateTicketBtnClicked,
        expanded = expandedFab,
        icon = {
            Icon(
                Icons.Filled.Add,
                stringResource(R.string.content_desc_create_ticket_button)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.create_ticket_btn_label)
            )
        },
    )
}

// Previews

@Preview(showBackground = true, group = "screen")
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HomeScreen(
            recordList = listOf(
                WeighbridgeRecord(
                    recordId = "SOME_ID_1",
                    entryDate = Date(),
                    fleetType = FleetType.INBOUND,
                    licenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 100.0,
                    tareWeight = 90.0,
                ),
                WeighbridgeRecord(
                    recordId = "SOME_ID_2",
                    entryDate = Date(),
                    fleetType = FleetType.OUTBOUND,
                    licenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 1525.0,
                    tareWeight = 1200.0,
                )
            ),
            appliedFilter = TicketListSortingParam(),
            onCreateTicketBtnClicked = {},
            onViewDetailsBtnClicked = {},
            onEditBtnClicked = {},
            onSortOptionSelected = { _, _ -> }
        )
    }
}

