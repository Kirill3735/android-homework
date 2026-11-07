package com.example.serversimulator

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorOffline = Color(0xFFEEEEEE)
private val ColorRunning = Color(0xFFE8F5E9)
private val ColorOverloaded = Color(0xFFFFF3E0)
private val ColorCrashed = Color(0xFFFFEBEE)
private val ColorWarning = Color(0xFFE65100)
private val ColorError = Color(0xFFB71C1C)

private fun backgroundFor(status: ServerStatus): Color = when (status) {
    ServerStatus.OFFLINE -> ColorOffline
    ServerStatus.RUNNING -> ColorRunning
    ServerStatus.OVERLOADED -> ColorOverloaded
    ServerStatus.CRASHED -> ColorCrashed
}

@Composable
fun ServerScreen(
    modifier: Modifier = Modifier,
    stateHolder: ServerStateHolder = remember { ServerStateHolder() }
) {
    ServerPanel(
        server = stateHolder.uiState,
        onNameChange = stateHolder::rename,
        onStart = stateHolder::start,
        onAddLoad = stateHolder::addLoad,
        onCoolDown = stateHolder::coolDown,
        onUpdateVersion = stateHolder::updateVersion,
        onReset = stateHolder::reset,
        modifier = modifier
    )
}

@Composable
fun ServerPanel(
    server: ServerUiState,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
    onAddLoad: () -> Unit,
    onCoolDown: () -> Unit,
    onUpdateVersion: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundFor(server.status))
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.server),
                contentDescription = "Серверная стойка",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .alpha(if (server.status == ServerStatus.OFFLINE) 0.4f else 1f)
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
                    color = ColorWarning
                )
            }
            if (server.status == ServerStatus.CRASHED) {
                Text(
                    text = "АВАРИЯ! Сервер упал от перегрева или предельной нагрузки. " +
                            "Охлаждение переведёт его в состояние «Остановлен».",
                    color = ColorError
                )
            }
            if (server.isTooHotToStart) {
                Text(
                    text = "Сервер остановлен, но слишком горячий (${server.temperature} °C). " +
                            "Охладите его ниже ${ServerRules.MAX_START_TEMP} °C для запуска.",
                    color = ColorWarning
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServerPanelPreview() {
    MaterialTheme {
        ServerPanel(
            server = ServerUiState(
                status = ServerStatus.OVERLOADED,
                cpuLoad = 80,
                temperature = 80
            ),
            onNameChange = {},
            onStart = {},
            onAddLoad = {},
            onCoolDown = {},
            onUpdateVersion = {},
            onReset = {}
        )
    }
}