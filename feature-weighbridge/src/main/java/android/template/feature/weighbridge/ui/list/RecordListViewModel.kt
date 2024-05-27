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

import android.template.core.data.model.SortingOption
import android.template.core.data.model.WeighbridgeRecord
import android.template.core.data.repository.WeighbridgeRecordRepository
import android.template.feature.weighbridge.ui.list.RecordListUiState.Loading
import android.template.feature.weighbridge.ui.list.RecordListUiState.Success
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordListViewModel @Inject constructor(
    private val recordRepository: WeighbridgeRecordRepository
) : ViewModel() {

    private val _filterParam = MutableStateFlow(TicketListSortingParam())
    val filterParam: StateFlow<TicketListSortingParam> = _filterParam

    val uiState: StateFlow<RecordListUiState> =
        filterParam
            .flatMapLatest {
                recordRepository.getAllWeighbridgeRecordsSortedBy(
                    it.sortingOption,
                    it.isAscending
                )
            }
            .map<List<WeighbridgeRecord>, RecordListUiState> { Success(data = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Loading)

    fun onSortOptionSelected(sortingOption: SortingOption, isAscending: Boolean) {
        viewModelScope.launch {
            _filterParam.value = TicketListSortingParam(sortingOption, isAscending)
        }
    }
}

sealed interface RecordListUiState {
    object Loading : RecordListUiState
    data class Error(val throwable: Throwable) : RecordListUiState
    data class Success(val data: List<WeighbridgeRecord>) : RecordListUiState
}
