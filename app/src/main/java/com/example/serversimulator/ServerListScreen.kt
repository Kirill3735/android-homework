package com.example.serversimulator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ServerListRoute(
    repo: ServerRepository,
    onOpenServer: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel = viewModel { ServerListViewModel(repo) }
    ServerListScreen(
        state = viewModel.uiState,
        onAdd = viewModel::onAdd,
        onDelete = viewModel::onDelete,
        onOpenServer = onOpenServer,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Composable
fun ServerListScreen(
    state: ServerListUiState,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit,
    onOpenServer: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Серверы: ${state.servers.size}",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
            Text("Добавить сервер")
        }

        if (state.servers.isEmpty()) {
            Text("Список пуст. Добавьте сервер.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.servers, key = { it.id }) { server ->
                    ServerRow(
                        server = server,
                        onClick = { onOpenServer(server.id) },
                        onDelete = { onDelete(server.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ServerRow(
    server: Server,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = ServerColors.backgroundFor(server.status)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = server.status.label,
                    color = ServerColors.labelFor(server.status)
                )
                Text(
                    text = "CPU ${server.cpuLoad}% · ${server.temperature} °C · v${server.version}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (server.isTooHotToStart) {
                    Text(
                        text = "Слишком горячий для запуска",
                        color = ServerColors.Warning,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            TextButton(onClick = onDelete) {
                Text("Удалить")
            }
        }
    }
}

@Preview(showBackground = true, name = "Список серверов")
@Composable
fun ServerListScreenPreview() {
    MaterialTheme {
        ServerListScreen(
            state = ServerListUiState(
                servers = listOf(
                    Server(id = 1),
                    Server(id = 2, status = ServerStatus.RUNNING, cpuLoad = 20, temperature = 40),
                    Server(id = 3, status = ServerStatus.OVERLOADED, cpuLoad = 80, temperature = 80),
                    Server(id = 4, status = ServerStatus.CRASHED, cpuLoad = 100, temperature = 100),
                    Server(id = 5, temperature = 75)
                )
            ),
            onAdd = {},
            onDelete = {},
            onOpenServer = {}
        )
    }
}

@Preview(showBackground = true, name = "Пустой список")
@Composable
fun ServerListEmptyPreview() {
    MaterialTheme {
        ServerListScreen(
            state = ServerListUiState(),
            onAdd = {},
            onDelete = {},
            onOpenServer = {}
        )
    }
}