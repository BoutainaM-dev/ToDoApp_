package com.boutaina.todo.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.boutaina.todo.R
import com.boutaina.todo.detail.DetailActivity
import com.boutaina.todo.detail.DetailActivity.Companion.TASK_KEY
import com.boutaina.todo.user.UserActivity
import com.boutaina.todo.user.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskListFragment : Fragment() {

    private lateinit var userTextView: TextView
    private lateinit var avatarImageView: ImageView
    private val viewModel: TaskListViewModel by activityViewModels() // or getViewModel() if Koin is used
    private val adapter = TaskListAdapter(
        onTaskEdit = { task -> onClickEdit(task) },  // Passer la fonction d'édition
        onTaskDelete = { task -> onClickDelete(task) } // Passer la fonction de suppression
    )
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)

        userTextView = rootView.findViewById(R.id.userTextView)
        avatarImageView = rootView.findViewById(R.id.avatarImageView)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.R1)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            createTaskLauncher.launch(intent)
        }

        avatarImageView.setOnClickListener {
            val intent = Intent(requireContext(), UserActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.tasksStateFlow.collect { newList ->
                adapter.submitList(newList) // Use submitList to properly update the list
            }
        }
        lifecycleScope.launch {
            userViewModel.userName.collect { name ->
                userTextView.text = name
            }
        }

        lifecycleScope.launch {
            userViewModel.userAvatarUri.collect { uri ->
                uri?.let { avatarImageView.load(it) }
            }
        }

        // Récupérer les infos de l'utilisateur au démarrage
        userViewModel.fetchUser()
    }

    private val createTaskLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = result.data?.getSerializableExtra(TASK_KEY) as? Task ?: return@registerForActivityResult
                viewModel.add(task)
                Toast.makeText(requireContext(), "Nouvelle tâche ajoutée : ${task.title}", Toast.LENGTH_SHORT).show()
            }
        }

    private val editTaskLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = result.data?.getSerializableExtra(TASK_KEY) as? Task ?: return@registerForActivityResult
                viewModel.update(task)
                Toast.makeText(requireContext(), "Tâche modifiée : ${task.title}", Toast.LENGTH_SHORT).show()
            }
        }

    private fun onClickDelete(task: Task) {
        viewModel.delete(task)
        Toast.makeText(requireContext(), "Tâche supprimée : ${task.title}", Toast.LENGTH_SHORT).show()
    }

    private fun onClickEdit(task: Task) {
        val jsonString = Json.encodeToString(task)
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(TASK_KEY, jsonString)
        }
        startActivity(intent)
    }
}
