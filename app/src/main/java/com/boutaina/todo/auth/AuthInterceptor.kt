package com.boutaina.todo.auth


import okhttp3.Interceptor
import okhttp3.Response
import com.boutaina.todo.data.Api

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = Api.TOKEN


        if (token == null) {

            throw AuthenticationException("Token is null, user needs to login.")
        }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}

class AuthenticationException(message: String) : Exception(message)
