package com.boutaina.todo.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.boutaina.todo.list.Task
import com.boutaina.todo.list.TaskListFragment
import com.google.gson.Gson
import java.util.*
import java.util.UUID


class DetailActivity : ComponentActivity() {

    companion object {
        const val TASK_KEY = "task"
    }

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            val initialTask = Task(
                id = UUID.randomUUID().toString().hashCode().toLong(),  // Accessing the most significant bits as a Long
                title = "Nouvelle tâche",
                description = sharedText ?: ""
            )
            setContent {
                DetailScreen(
                    initialTask = initialTask,
                    onValidate = { title, description ->
                        val updatedTask = initialTask.copy(title = title, description = description)
                        val resultIntent = Intent().apply {
                            val taskJson = gson.toJson(updatedTask) // ✅ Serialize with Gson
                            putExtra(TASK_KEY, taskJson)  // Utiliser la constante de DetailActivity
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        } else {
            val taskJson = intent.getStringExtra(TASK_KEY) // Utiliser la constante de DetailActivity
            val initialTask = taskJson?.let { gson.fromJson(it, Task::class.java) } // ✅ Deserialize with Gson

            setContent {
                DetailScreen(
                    initialTask = initialTask,
                    onValidate = { title, description ->
                        val updatedTask = initialTask?.copy(title = title, description = description)
                            ?: Task(id = UUID.randomUUID().toString().hashCode().toLong(), title = title, description = description)

                        val resultIntent = Intent().apply {
                            val taskJson = gson.toJson(updatedTask) // ✅ Serialize with Gson
                            putExtra(TASK_KEY, taskJson)  // Utiliser la constante de DetailActivity
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        }
    }
}


@Composable
fun DetailScreen(onValidate: (String, String) -> Unit, initialTask: Task?) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialTask == null) "Nouvelle tâche" else "Éditer la tâche",
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onValidate(title, description) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Valider")
        }
    }
}
