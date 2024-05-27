package android.template.feature.weighbridge.ui.list

import android.template.core.data.model.SortingOption

data class TicketListSortingParam(
    val sortingOption: SortingOption = SortingOption.DATE,
    val isAscending: Boolean = false
)