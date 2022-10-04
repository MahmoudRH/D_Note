package com.mahmoudrh.roomxml.presentation.ui_components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mahmoudrh.roomxml.domain.utils.OrderBy
import com.mahmoudrh.roomxml.domain.utils.OrderType


@Preview
@Composable
fun OrderSection(
    order: OrderBy = OrderBy.Date(OrderType.Descending),
    onOrderChange: (OrderBy) -> Unit = {}
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Divider(
                color = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
            )
            Column(
                modifier = Modifier
                    .weight(10f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Sort By: ")
                    DefaultRadioButton(
                        text = "Title",
                        selected = order is OrderBy.Title,
                        onSelect = { onOrderChange(OrderBy.Title(order.orderType)) }
                    )
                    DefaultRadioButton(
                        text = "Date",
                        selected = order is OrderBy.Date,
                        onSelect = { onOrderChange(OrderBy.Date(order.orderType)) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Sort Type: ")
                    DefaultRadioButton(
                        text = "Asc",
                        selected = order.orderType is OrderType.Ascending,
                        onSelect = {
                            onOrderChange(order.copy(OrderType.Ascending))
                        }
                    )
                    DefaultRadioButton(
                        text = "Desc",
                        selected = order.orderType is OrderType.Descending,
                        onSelect = {
                            onOrderChange(order.copy(OrderType.Descending))
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun DefaultRadioButton(text: String, selected: Boolean, onSelect: () -> Unit) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text)
        Spacer(modifier = Modifier.width(4.dp))
        RadioButton(selected = selected, onClick = onSelect)
    }

}
