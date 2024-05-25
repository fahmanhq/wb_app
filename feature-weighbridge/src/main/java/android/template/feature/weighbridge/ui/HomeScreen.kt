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

package android.template.feature.weighbridge.ui

import android.template.core.ui.MyApplicationTheme
import android.template.core.ui.Typography
import android.template.feature.weighbridge.ui.MyModelUiState.Success
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.repeatOnLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.template.core.ui.R as CoreUiR

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: MyModelViewModel = hiltViewModel()) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val items by produceState<MyModelUiState>(
        initialValue = MyModelUiState.Loading,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }
    if (items is Success) {
//        HomeScreen(
//            items = (items as Success).data,
//            onSave = { name -> viewModel.addMyModel(name) },
//            modifier = modifier
//        )

        HomeScreen(
            recordList = listOf(
                WeighbridgeRecordSpec(
                    id = "SOME_ID_1",
                    dateTime = Date(),
                    fleetType = FleetType.Inbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 100.0,
                    netWeight = 90.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_2",
                    dateTime = Date(),
                    fleetType = FleetType.Outbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 1525.0,
                    netWeight = 1200.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_1",
                    dateTime = Date(),
                    fleetType = FleetType.Inbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 100.0,
                    netWeight = 90.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_2",
                    dateTime = Date(),
                    fleetType = FleetType.Outbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 1525.0,
                    netWeight = 1200.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_1",
                    dateTime = Date(),
                    fleetType = FleetType.Inbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 100.0,
                    netWeight = 90.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_2",
                    dateTime = Date(),
                    fleetType = FleetType.Outbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 1525.0,
                    netWeight = 1200.0,
                ),
            ), onSave = {})
    }
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    recordList: List<WeighbridgeRecordSpec>,
    onSave: (name: String) -> Unit
) {
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* do something */ },
                expanded = expandedFab,
                icon = { Icon(Icons.Filled.Add, "Localized Description") },
                text = { Text(text = "Create Ticket") },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recordList) {
                RecordCard(spec = it)
            }
        }
    }
}

enum class FleetType {
    Inbound,
    Outbound
}

data class WeighbridgeRecordSpec(
    val id: String,
    val dateTime: Date,
    val fleetType: FleetType,
    val truckLicenseNumber: String,
    val driverName: String,
    val grossWeight: Double,
    val netWeight: Double,
    val notes: String? = null
)

@Composable
private fun RecordCard(
    modifier: Modifier = Modifier,
    spec: WeighbridgeRecordSpec
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White,
            contentColor = Color.DarkGray
        ),
        border = BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val labelBackgroundColor = when (spec.fleetType) {
                FleetType.Inbound -> Color.Green
                FleetType.Outbound -> Color.Cyan
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Record #SOME_ID",
                    style = Typography.titleMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                val fleetIconResId = when (spec.fleetType) {
                    FleetType.Inbound -> CoreUiR.drawable.ic_inbound
                    FleetType.Outbound -> CoreUiR.drawable.ic_outbound
                }
                val fleetTypeLabel = when (spec.fleetType) {
                    FleetType.Inbound -> "INBOUND"
                    FleetType.Outbound -> "OUTBOUND"
                }
                TextWithIcon(
                    modifier = Modifier
                        .background(labelBackgroundColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = fleetTypeLabel,
                    textStyle = Typography.titleSmall,
                    icon = ImageVector.vectorResource(id = fleetIconResId),
                    iconSize = 16.dp,
                )
            }

            val formatter = remember {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            }
            Text(
                text = "at " + formatter.format(spec.dateTime),
                style = Typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFECECEC))

            FieldRow(title = "Truck License Number:", value = spec.truckLicenseNumber)
            FieldRow(title = "Driver Name:", value = spec.driverName)

            val weightFormatter = remember { WeightFormatter() }
            FieldRow(title = "Gross Weight:", value = weightFormatter.format(spec.grossWeight))
            FieldRow(title = "Net Weight:", value = weightFormatter.format(spec.netWeight))
            val notes = spec.notes.orEmpty()
            if (notes.isNotBlank()) {
                FieldRow(title = "Notes:", value = notes)
            }
        }
    }
}

@Composable
private fun FieldRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = Typography.bodySmall,
        )
    }
}

// Previews

@Preview(showBackground = true, group = "screen")
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HomeScreen(
            recordList = listOf(
                WeighbridgeRecordSpec(
                    id = "SOME_ID_1",
                    dateTime = Date(),
                    fleetType = FleetType.Inbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 100.0,
                    netWeight = 90.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_2",
                    dateTime = Date(),
                    fleetType = FleetType.Outbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 1525.0,
                    netWeight = 1200.0,
                )
            ), onSave = {})
    }
}

@Preview(showBackground = true, widthDp = 480, group = "screen")
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        HomeScreen(
            recordList = listOf(
                WeighbridgeRecordSpec(
                    id = "SOME_ID_1",
                    dateTime = Date(),
                    fleetType = FleetType.Inbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 100.0,
                    netWeight = 90.0,
                ),
                WeighbridgeRecordSpec(
                    id = "SOME_ID_2",
                    dateTime = Date(),
                    fleetType = FleetType.Outbound,
                    truckLicenseNumber = "F 1231 ABC",
                    driverName = "William Doe",
                    grossWeight = 1525.0,
                    netWeight = 1200.0,
                )
            ), onSave = {})
    }
}

@Preview(group = "component")
@Composable
private fun RecordCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecordCard(
            spec = WeighbridgeRecordSpec(
                id = "SOME_ID_1",
                dateTime = Date(),
                fleetType = FleetType.Inbound,
                truckLicenseNumber = "F 1231 ABC",
                driverName = "William Doe",
                grossWeight = 100.0,
                netWeight = 90.0,
            )
        )

        RecordCard(
            spec = WeighbridgeRecordSpec(
                id = "SOME_ID_2",
                dateTime = Date(),
                fleetType = FleetType.Outbound,
                truckLicenseNumber = "F 1231 ABC",
                driverName = "William Doe",
                grossWeight = 1525.0,
                netWeight = 1200.0,
            )
        )
    }
}