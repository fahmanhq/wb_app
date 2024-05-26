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

package android.template.ui

import android.template.feature.weighbridge.ui.FormMode
import android.template.feature.weighbridge.ui.HomeScreen
import android.template.feature.weighbridge.ui.TicketFormScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            HomeScreen(
                navController = navController,
                onCreateTicketBtnClicked = {
                    navController.navigate("ticket-form")
                },
                onViewDetailsBtnClicked = {
                    navController.navigate("ticket-form?recordId=$it&mode=${FormMode.VIEW.name}")
                },
                onEditBtnClicked = {
                    navController.navigate("ticket-form?recordId=$it&mode=${FormMode.EDIT.name}")
                },
            )
        }
        composable(
            route = "ticket-form?recordId={recordId}&mode={mode}",
            arguments = listOf(
                navArgument("recordId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = FormMode.CREATE.name
                }
            )
        ) {
            val recordId = it.arguments?.getString("recordId")
            val mode = it.arguments?.getString("mode")?.let { FormMode.valueOf(it) }!!

            TicketFormScreen(
                navController = navController,
                recordId = recordId,
                mode = mode,
                onBackButtonClicked = { navController.popBackStack() },
                onRecordSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}
