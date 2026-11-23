package com.example.serversimulator

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

@Serializable
data object ServerListKey : NavKey

@Serializable
data class ServerKey(val serverId: Int) : NavKey

@Composable
fun ServerNavigation(
    repo: ServerRepository,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(ServerListKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<ServerListKey> {
                ServerListRoute(
                    repo = repo,
                    onOpenServer = { id ->
                        if (backStack.lastOrNull() == ServerListKey) {
                            backStack.add(ServerKey(id))
                        }
                    },
                    contentPadding = contentPadding
                )
            }
            entry<ServerKey> { key ->
                ServerRoute(
                    serverId = key.serverId,
                    repo = repo,
                    onBack = {
                        if (backStack.lastOrNull() == key) backStack.removeLastOrNull()
                    },
                    contentPadding = contentPadding
                )
            }
        },
    )
}