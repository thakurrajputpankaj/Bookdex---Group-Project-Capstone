package com.pankaj.bookdex_capstone

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Response

class Browse : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var search_edittext: EditText
    private lateinit var search: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        recyclerView = findViewById(R.id.books_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        search_edittext = findViewById(R.id.search_edittext)
        search = findViewById(R.id.search)

        search.setOnClickListener{
            fetchBooks(search_edittext.text.toString())
        }
        fetchBooks("Kotlin programming")
    }

    private fun fetchBooks(query: String) {
        RetrofitClient.service.getBooks(query).enqueue(object : retrofit2.Callback<BookResponse> {
            override fun onResponse(call: retrofit2.Call<BookResponse>, response: retrofit2.Response<BookResponse>) {
                if (response.isSuccessful) {
                    val booksAdapter = BooksAdapter(response.body()?.items ?: listOf())
                    recyclerView.adapter = booksAdapter
                } else {
                    Log.e("BooksActivity", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<BookResponse>, t: Throwable) {
                Log.e("BooksActivity", "Failure: ${t.message}")
            }
        })
    }
}
