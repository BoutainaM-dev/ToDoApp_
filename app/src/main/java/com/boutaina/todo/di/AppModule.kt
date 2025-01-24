/*package com.boutaina.todo.di

import com.boutaina.todo.auth.TokenRepository
import com.boutaina.todo.list.TaskListViewModel
import com.boutaina.todo.user.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Define TokenRepository as a singleton
    single { TokenRepository(get()) }

    // Define ViewModels
    viewModel<TaskListViewModel> { TaskListViewModel(get()) }
    viewModel<UserViewModel> { UserViewModel(get()) }
}*/
