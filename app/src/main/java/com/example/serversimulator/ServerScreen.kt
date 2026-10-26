package com.example.serversimulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServerScreen(modifier: Modifier = Modifier) {
    var server by remember { mutableStateOf(ServerNode()) }

    ServerPanel(
        server = server,
        onNameChange = { server = server.copy(name = it) },
        onStart = { server = server.start() },
        onAddLoad = { server = server.addLoad() },
        onCoolDown = { server = server.coolDown() },
        onUpdateVersion = { server = server.updateVersion() },
        onReset = { server = ServerNode() },
        modifier = modifier
    )
}

@Composable
fun ServerPanel(
    server: ServerNode,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
    onAddLoad: () -> Unit,
    onCoolDown: () -> Unit,
    onUpdateVersion: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when (server.status) {
        ServerStatus.OFFLINE -> Color(0xFFEEEEEE)
        ServerStatus.RUNNING -> Color(0xFFE8F5E9)
        ServerStatus.OVERLOADED -> Color(0xFFFFF3E0)
        ServerStatus.CRASHED -> Color(0xFFFFEBEE)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Сервер: ${server.name}", fontSize = 22.sp)

        TextField(
            value = server.name,
            onValueChange = onNameChange,
            label = { Text("Имя сервера") },
            singleLine = true,
            enabled = server.status != ServerStatus.CRASHED,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Состояние: ${server.status.label}", fontSize = 18.sp)
        Text(text = "Загрузка CPU: ${server.cpuLoad}%", fontSize = 16.sp)
        Text(text = "Температура: ${server.temperature} °C", fontSize = 16.sp)
        Text(text = "Версия ПО: v${server.version}", fontSize = 16.sp)

        if (server.status == ServerStatus.OVERLOADED) {
            Text(
                text = "Внимание! Перегрузка сервера. Обновление ПО заблокировано.",
                color = Color(0xFFE65100)
            )
        }
        if (server.status == ServerStatus.CRASHED) {
            Text(
                text = "АВАРИЯ! Сервер упал от перегрева или 100% нагрузки. Охладите его для перевода в режим «Остановлен» или выполните полный сброс.",
                color = Color(0xFFB71C1C)
            )
        }

        Button(
            onClick = onStart,
            enabled = server.canStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить сервер")
        }

        Button(
            onClick = onAddLoad,
            enabled = server.canAddLoad,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Дать нагрузку (+30% CPU, +20 °C)")
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
            Text("Обновить ПО (нужен CPU ≤ 60% и t ≤ 65 °C)")
        }

        Button(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить в исходное состояние")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServerPanelPreview() {
    MaterialTheme {
        ServerPanel(
            server = ServerNode(status = ServerStatus.OVERLOADED, cpuLoad = 80, temperature = 80),
            onNameChange = {},
            onStart = {},
            onAddLoad = {},
            onCoolDown = {},
            onUpdateVersion = {},
            onReset = {}
        )
    }
}