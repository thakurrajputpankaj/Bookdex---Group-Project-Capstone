package com.pankaj.bookdex_capstone

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApiService {
    @GET("books/v1/volumes")
    fun getBooks(@Query("q") query: String): Call<BookResponse>
}

