package com.wanandroid.compose.bookmark

import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.api.BookMarkApi
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookMarkApiContractTest {
    @Test
    fun `list deserializes server fields and mutations send correct forms`() = runBlocking {
        val requests = mutableListOf<Request>()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            requests += request
            val data = if (request.method == "GET") {
                """[{"id":5,"name":"Android","link":"https://developer.android.com","icon":"","order":0,"visible":1}]"""
            } else "null"
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("""{"data":$data,"errorCode":0,"errorMsg":""}""".toResponseBody())
                .build()
        }.build()
        val api = Retrofit.Builder()
            .baseUrl("https://wanandroid.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookMarkApi::class.java)

        assertEquals(listOf(BookMarkItem(5, "Android", "https://developer.android.com")), api.getBookMarks().data)
        assertEquals("GET", requests.last().method)
        assertEquals("/lg/collect/usertools/json", requests.last().url.encodedPath)

        api.addBookMark("Android & Compose", "https://example.com/?a=1&b=2")
        assertForm(requests.last(), "/lg/collect/addtool/json", mapOf("name" to "Android & Compose", "link" to "https://example.com/?a=1&b=2"))
        api.updateBookMark(5, "Updated", "https://example.com/new")
        assertForm(requests.last(), "/lg/collect/updatetool/json", mapOf("id" to "5", "name" to "Updated", "link" to "https://example.com/new"))
        api.deleteBookMark(5)
        assertForm(requests.last(), "/lg/collect/deletetool/json", mapOf("id" to "5"))
    }

    private fun assertForm(request: Request, path: String, fields: Map<String, String>) {
        assertEquals("POST", request.method)
        assertEquals(path, request.url.encodedPath)
        val body = request.body as FormBody
        assertEquals(fields, (0 until body.size).associate { body.name(it) to body.value(it) })
    }
}
