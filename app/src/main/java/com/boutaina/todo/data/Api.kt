package com.boutaina.todo.data

import com.boutaina.todo.auth.AuthInterceptor
import com.boutaina.todo.data.TasksWebService
import com.boutaina.todo.data.UserWebService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    var TOKEN: String? = null
    val userWebService: UserWebService by lazy {
        retrofit.create(UserWebService::class.java)
    }

    private val retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            // Add your custom interceptor for logging (optional)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            // Add your AuthInterceptor that will handle token insertion
            .addInterceptor(AuthInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.todoist.com/")
            .client(okHttpClient)  // Use the OkHttpClient with the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val tasksWebService: TasksWebService by lazy {
        retrofit.create(TasksWebService::class.java)
    }

    // Function to initialize the token
    fun initializeToken(token: String?) {
        TOKEN = token
    }
}
