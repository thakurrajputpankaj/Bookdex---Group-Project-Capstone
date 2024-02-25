package com.pankaj.bookdex_capstone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Browse : AppCompatActivity(), BooksAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseReference: DatabaseReference

    private lateinit var search: ImageView
    private lateinit var search_edittext: EditText

    private lateinit var hamburger : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        recyclerView = findViewById(R.id.books_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        drawerLayout = findViewById(R.id.drawer_layout)
        search = findViewById(R.id.search)
        search_edittext = findViewById(R.id.search_edittext)
        hamburger = findViewById(R.id.hamburger)

        hamburger.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }

        databaseReference = FirebaseDatabase.getInstance().reference.child("favourites")

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.bringToFront()
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_favourites -> {
                    val intent = Intent(this, FavouritesActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_notification -> {
                    //val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        search.setOnClickListener{
            fetchBooks(search_edittext.text.toString())
        }
        fetchBooks("Kotlin programming")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchBooks(query: String) {
        RetrofitClient.service.getBooks(query).enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    val booksAdapter = BooksAdapter(response.body()?.items ?: listOf(), this@Browse)
                    recyclerView.adapter = booksAdapter
                } else {
                    Log.e("BooksActivity", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Log.e("BooksActivity", "Failure: ${t.message}")
            }
        })
    }

    override fun onItemClick(book: BookItem) {
        val bookData = mapOf(
            "title" to book.volumeInfo.title,
            "author" to (book.volumeInfo.authors?.joinToString(", ") ?: "Author Unknown"),
            "image" to book.volumeInfo.imageLinks?.thumbnail
        )

        databaseReference.push().setValue(bookData)

        Toast.makeText(this, "Book saved to Firebase Database", Toast.LENGTH_SHORT).show()
    }



}
