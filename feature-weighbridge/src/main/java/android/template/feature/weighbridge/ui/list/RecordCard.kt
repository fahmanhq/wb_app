package android.template.feature.weighbridge.ui.list

import android.template.core.data.model.FleetType
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.ui.Pink80
import android.template.core.ui.Purple80
import android.template.core.ui.Typography
import android.template.core.ui.component.TextWithIcon
import android.template.core.ui.utils.RecordIdFormatter
import android.template.core.ui.utils.WeightFormatter
import android.template.feature.weighbridge.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun RecordCard(
    modifier: Modifier = Modifier,
    spec: WeighbridgeRecord,
    onViewDetailsBtnClicked: (String) -> Unit = {},
    onEditBtnClicked: (String) -> Unit = {}
) {
    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onViewDetailsBtnClicked.invoke(spec.recordId)
            },
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
                FleetType.INBOUND -> Purple80
                FleetType.OUTBOUND -> Pink80
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.ticket_id_title,
                        RecordIdFormatter.format(spec.recordId)
                    ),
                    style = Typography.titleMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                val fleetIconResId = when (spec.fleetType) {
                    FleetType.INBOUND -> android.template.core.ui.R.drawable.ic_inbound
                    FleetType.OUTBOUND -> android.template.core.ui.R.drawable.ic_outbound
                }
                val fleetTypeLabel = when (spec.fleetType) {
                    FleetType.INBOUND -> stringResource(R.string.inbound_indicator_label)
                    FleetType.OUTBOUND -> stringResource(R.string.outbound_indicator_label)
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

            Text(
                text = stringResource(
                    R.string.card_entry_time_template,
                    dateFormatter.format(spec.entryDate)
                ),
                style = Typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFECECEC))

            FieldRow(
                title = stringResource(R.string.card_truck_license_number_field),
                value = spec.licenseNumber
            )
            FieldRow(
                title = stringResource(R.string.card_driver_name_field),
                value = spec.driverName
            )

            val netWeight by remember {
                derivedStateOf {
                    if (spec.tareWeight > 0) {
                        spec.grossWeight - spec.tareWeight
                    } else {
                        spec.grossWeight
                    }
                }
            }
            FieldRow(
                title = stringResource(R.string.card_gross_weight_field),
                value = WeightFormatter.format(spec.grossWeight)
            )
            FieldRow(
                title = stringResource(R.string.card_net_weight_field),
                value = WeightFormatter.format(netWeight)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(2f),
                    onClick = { onViewDetailsBtnClicked(spec.recordId) }
                ) {
                    Text(text = stringResource(R.string.card_view_ticket_details_btn_label))
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onEditBtnClicked(spec.recordId) }
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp),
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null
                    )
                    Text(text = stringResource(R.string.card_edit_ticket_btn_label))
                }
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


@Preview(group = "component")
@Composable
private fun RecordCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecordCard(
            spec = WeighbridgeRecord(
                recordId = "SOME_ID_1",
                entryDate = Date(),
                fleetType = FleetType.INBOUND,
                licenseNumber = "F 1231 ABC",
                driverName = "William Doe",
                grossWeight = 100.0,
                tareWeight = 90.0,
            )
        )

        RecordCard(
            spec = WeighbridgeRecord(
                recordId = "SOME_ID_2",
                entryDate = Date(),
                fleetType = FleetType.OUTBOUND,
                licenseNumber = "F 1231 ABC",
                driverName = "William Doe",
                grossWeight = 1525.0,
                tareWeight = 1200.0,
            )
        )
    }
}