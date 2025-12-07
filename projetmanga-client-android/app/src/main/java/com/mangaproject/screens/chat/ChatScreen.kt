package com.mangaproject.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.repository.AiRepository

@Composable
fun ChatScreen() {

    // Création des dépendances ici (simple, sans DI framework)
    val api = remember { RetrofitInstance.apiService }
    val aiRepo = remember { AiRepository(api) }

    val vm: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(aiRepo)
    )

    val messages by vm.messages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                Text(
                    text = (if (msg.fromUser) "🧑‍💬 " else "🤖 ") + msg.text,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        var input by remember { mutableStateOf("") }

        Row {
            TextField(
                modifier = Modifier.weight(1f),
                value = input,
                onValueChange = { input = it }
            )
            Button(
                onClick = {
                    vm.sendMessage(input)
                    input = ""
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Envoyer")
            }
        }
    }
}
