package com.boutaina.todo.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.boutaina.todo.R
import com.boutaina.todo.list.Task

class AddTaskActivity : AppCompatActivity() {

    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val titleInput = findViewById<EditText>(R.id.title_input)
        val descriptionInput = findViewById<EditText>(R.id.description_input)
        val saveButton = findViewById<Button>(R.id.save_button)

        // Vérifie si on modifie une tâche existante
        taskId = intent.getStringExtra("task_id")
        titleInput.setText(intent.getStringExtra("task_title"))
        descriptionInput.setText(intent.getStringExtra("task_description"))

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            if (title.isNotBlank()) {
                val resultIntent = Intent().apply {
                    putExtra("task_id", taskId)
                    putExtra("task_title", title)
                    putExtra("task_description", description)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                titleInput.error = "Le titre est requis"
            }
        }
    }
}
