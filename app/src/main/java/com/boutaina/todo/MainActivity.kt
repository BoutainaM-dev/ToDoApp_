package com.boutaina.todo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boutaina.todo.add.AddTaskActivity
import com.boutaina.todo.auth.LoginActivity
import com.boutaina.todo.auth.TokenRepository
import com.boutaina.todo.data.Api
import com.boutaina.todo.list.Task
import com.boutaina.todo.list.TaskListAdapter
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val tokenRepository by lazy { TokenRepository(this) }
    private val userWebService = Api.userWebService
    private val tasksWebService = Api.tasksWebService

    private lateinit var userName: TextView
    private lateinit var userImage: ImageView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskListAdapter
    private lateinit var addTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation des vues
        addTaskButton = findViewById(R.id.add_task_button)
        userName = findViewById(R.id.userName)
        userImage = findViewById(R.id.userImage)
        taskRecyclerView = findViewById(R.id.taskRecyclerView)

        // Configuration du RecyclerView
        taskAdapter = TaskListAdapter(
            onTaskEdit = { editTask(it) },
            onTaskDelete = { deleteTask(it) }
        )
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        // Gestion du bouton d'ajout de tâche
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }

        // Vérification du token et chargement des données
        lifecycleScope.launch {
            tokenRepository.tokenFlow.collect { token ->
                if (token != null) {
                    loadUserData()
                    loadTaskList()
                } else {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    // Utilisation de la nouvelle API pour gérer l'ajout d'une tâche
    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("task_title") ?: return@registerForActivityResult
            val description = data.getStringExtra("task_description") ?: ""

            val newTask = Task(id = 0L, title = title, description = description)

            lifecycleScope.launch {
                val response = tasksWebService.create(newTask)
                if (response.isSuccessful) {
                    loadTaskList()
                } else {
                    Toast.makeText(this@MainActivity, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val response = userWebService.fetchUser()
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        userName.text = it.name
                        Glide.with(this@MainActivity)
                            .load(it.avatar)
                            .into(userImage)
                    }
                } else {
                    Log.e("MainActivity", "Erreur lors de la récupération des données utilisateur")
                    Toast.makeText(this@MainActivity, "Erreur lors de la récupération des données utilisateur", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
                Toast.makeText(this@MainActivity, "Échec du chargement des données utilisateur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTaskList() {
        lifecycleScope.launch {
            try {
                val response = tasksWebService.fetchTasks()
                if (response.isSuccessful) {
                    val tasks = response.body()
                    tasks?.let {
                        taskAdapter.submitList(it)
                    }
                } else {
                    Log.e("MainActivity", "Erreur lors de la récupération des tâches")
                    Toast.makeText(this@MainActivity, "Erreur lors de la récupération des tâches", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
                Toast.makeText(this@MainActivity, "Échec du chargement des tâches", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTask(task: Task) {
        lifecycleScope.launch {
            val response = tasksWebService.delete(task.id!!)
            if (response.isSuccessful) {
                loadTaskList()
            } else {
                Toast.makeText(this@MainActivity, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editTask(task: Task) {
        val intent = Intent(this, AddTaskActivity::class.java).apply {
            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
            putExtra("task_description", task.description)
        }
        addTaskLauncher.launch(intent)
    }
}
