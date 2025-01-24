package com.boutaina.todo.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun Bitmap.toRequestBody(): MultipartBody.Part {
    val tmpFile = File.createTempFile("avatar", "jpg")
    tmpFile.outputStream().use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.jpg",
        body = tmpFile.readBytes().toRequestBody()
    )
}
    fun Uri.toRequestBody(context: Context): MultipartBody.Part {
            val fileInputStream = context.contentResolver.openInputStream(this)!!
            val fileBody = fileInputStream.readBytes().toRequestBody()  // Convert the stream to RequestBody
            return MultipartBody.Part.createFormData(
                name = "avatar",
                filename = "avatar.jpg",  // You can choose a dynamic name or extension here
                body = fileBody
            )
        }


