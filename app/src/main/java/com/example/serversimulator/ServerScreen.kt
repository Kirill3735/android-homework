package com.example.serversimulator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val IMAGE_HEIGHT = 140.dp
private const val IMAGE_ALPHA_OFFLINE = 0.4f
@Composable
fun ServerRoute(
    serverId: Int,
    repo: ServerRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel = viewModel { ServerViewModel(serverId, repo) }
    ServerScreen(
        state = viewModel.uiState,
        onNameChange = viewModel::onRename,
        onStart = viewModel::onStart,
        onAddLoad = viewModel::onAddLoad,
        onCoolDown = viewModel::onCoolDown,
        onUpdateVersion = viewModel::onUpdateVersion,
        onReset = viewModel::onReset,
        onBack = onBack,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Composable
fun ServerScreen(
    state: ServerUiState,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
    onAddLoad: () -> Unit,
    onCoolDown: () -> Unit,
    onUpdateVersion: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val server = state.server
    if (server == null) {
        ServerNotFound(onBack = onBack, modifier = modifier, contentPadding = contentPadding)
    } else {
        ServerDetails(
            server = server,
            onNameChange = onNameChange,
            onStart = onStart,
            onAddLoad = onAddLoad,
            onCoolDown = onCoolDown,
            onUpdateVersion = onUpdateVersion,
            onReset = onReset,
            onBack = onBack,
            modifier = modifier,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun ServerNotFound(
    onBack: () -> Unit,
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
            text = "Сервер не найден",
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedButton(onClick = onBack) {
            Text("К списку серверов")
        }
    }
}

@Composable
private fun ServerDetails(
    server: Server,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
    onAddLoad: () -> Unit,
    onCoolDown: () -> Unit,
    onUpdateVersion: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ServerColors.backgroundFor(server.status))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.server),
                contentDescription = "Серверная стойка",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IMAGE_HEIGHT)
                    .alpha(if (server.status == ServerStatus.OFFLINE) IMAGE_ALPHA_OFFLINE else 1f)
            )

            Text(text = "Сервер: ${server.name}", fontSize = 22.sp)

            TextField(
                value = server.name,
                onValueChange = onNameChange,
                label = { Text("Имя сервера") },
                singleLine = true,
                enabled = server.canRename,
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "Состояние: ${server.status.label}", fontSize = 18.sp)
            Text(text = "Загрузка CPU: ${server.cpuLoad}%", fontSize = 16.sp)
            Text(text = "Температура: ${server.temperature} °C", fontSize = 16.sp)
            Text(text = "Версия ПО: v${server.version}", fontSize = 16.sp)

            if (server.status == ServerStatus.OVERLOADED) {
                Text(
                    text = "Внимание! Перегрузка сервера. Обновление ПО заблокировано.",
                    color = ServerColors.Warning
                )
            }
            if (server.status == ServerStatus.CRASHED) {
                Text(
                    text = "АВАРИЯ! Сервер упал от перегрева или предельной нагрузки. " +
                            "Охлаждение переведёт его в состояние «Остановлен».",
                    color = ServerColors.Error
                )
            }
            if (server.isTooHotToStart) {
                Text(
                    text = "Сервер остановлен, но слишком горячий (${server.temperature} °C). " +
                            "Охладите его ниже ${ServerRules.MAX_START_TEMP} °C для запуска.",
                    color = ServerColors.Warning
                )
            }

            Button(
                onClick = onStart,
                enabled = server.canStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Запустить сервер (нужна t < ${ServerRules.MAX_START_TEMP} °C)")
            }

            Button(
                onClick = onAddLoad,
                enabled = server.canAddLoad,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Дать нагрузку (+${ServerRules.LOAD_CPU_STEP}% CPU, " +
                            "+${ServerRules.LOAD_TEMP_STEP} °C)"
                )
            }

            Button(
                onClick = onCoolDown,
                enabled = server.canCoolDown,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Охладить и разгрузить")
            }

            Button(
                onClick = onUpdateVersion,
                enabled = server.canUpdateVersion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Обновить ПО (CPU ≤ ${ServerRules.MAX_UPDATE_CPU}%, " +
                            "t ≤ ${ServerRules.MAX_UPDATE_TEMP} °C)"
                )
            }

            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сбросить в исходное состояние")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("К списку серверов")
            }
        }
    }
}

@Preview(showBackground = true, name = "Перегрузка")
@Composable
fun ServerScreenOverloadedPreview() {
    MaterialTheme {
        ServerScreen(
            state = ServerUiState(
                server = Server(
                    id = 3,
                    status = ServerStatus.OVERLOADED,
                    cpuLoad = 80,
                    temperature = 80
                )
            ),
            onNameChange = {},
            onStart = {},
            onAddLoad = {},
            onCoolDown = {},
            onUpdateVersion = {},
            onReset = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Сервер не найден")
@Composable
fun ServerScreenNotFoundPreview() {
    MaterialTheme {
        ServerScreen(
            state = ServerUiState(server = null),
            onNameChange = {},
            onStart = {},
            onAddLoad = {},
            onCoolDown = {},
            onUpdateVersion = {},
            onReset = {},
            onBack = {}
        )
    }
}