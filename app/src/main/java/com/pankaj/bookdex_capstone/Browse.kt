package com.pankaj.bookdex_capstone

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
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
    private lateinit var rootReference: DatabaseReference

    private lateinit var search: ImageView
    private lateinit var search_edittext: EditText

    private lateinit var hamburger : ImageView
    private lateinit var booksAdapter: BooksAdapter

    val mutableBookItems = mutableListOf<BookItem>()
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
        rootReference = FirebaseDatabase.getInstance().reference

        booksAdapter = BooksAdapter(mutableListOf(), this@Browse, rootReference)
        recyclerView.adapter = booksAdapter

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
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        search.setOnClickListener{
            val query = search_edittext.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchBooks(query)
            } else {
                Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
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
                    val bookItems = response.body()?.items ?: listOf()
                    mutableBookItems.clear()
                    mutableBookItems.addAll(bookItems)
                    booksAdapter.updateDataset(mutableBookItems);
                } else {
                    Log.e("BooksActivity", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Log.e("BooksActivity", "Failure: ${t.message}")
            }
        })
    }


    override fun onItemClick(book: BookItem, isFavourite: Boolean, position: Int) {
        //Log.d("Browse", "Item clicked at position: $position")
        if (isFavourite) {
            removeFromFavourites(book, position)
        } else {
            addToFavourites(book, position)
        }
    }



    private fun addToFavourites(book: BookItem, position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Add to Favourites")
            setMessage("Are you sure you want to add this to Favourites ?")
            setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                val key = book.id
                val bookData = mapOf(
                    "title" to book.volumeInfo.title,
                    "author" to (book.volumeInfo.authors?.joinToString(", ") ?: "Author Unknown"),
                    "image" to book.volumeInfo.imageLinks?.thumbnail
                )
                databaseReference.child(key).setValue(bookData)
                showToast("Book saved to Favourites Section")
                val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
                if (viewHolder != null && viewHolder is BooksAdapter.BookViewHolder) {
                    viewHolder.buttonFavourites.text = "Remove from Favourites"
                    viewHolder.buttonFavourites.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.red))
                    booksAdapter.notifyItemChanged(position)
                }
            }
            setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
                if (viewHolder != null && viewHolder is BooksAdapter.BookViewHolder) {
                    viewHolder.buttonFavourites.text = "Add to Favourites"
                    viewHolder.buttonFavourites.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.colorPrimaryVariant))
                    booksAdapter.notifyItemChanged(position)
                }
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun removeFromFavourites(book: BookItem, position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Remove from Favourites")
            setMessage("Are you sure you want to remove this from Favourites ?")
            setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                val key = book.id
                databaseReference.child(key).removeValue()
                showToast("Book removed from Favourites Section")

                val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
                if (viewHolder != null && viewHolder is BooksAdapter.BookViewHolder) {
                    viewHolder.buttonFavourites.text = "Add to Favourites"
                    viewHolder.buttonFavourites.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.colorPrimaryVariant))

                    booksAdapter.notifyItemChanged(position)
                }
            }
            setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
                if (viewHolder != null && viewHolder is BooksAdapter.BookViewHolder) {
                    viewHolder.buttonFavourites.text = "Remove from Favourites"
                    viewHolder.buttonFavourites.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.red))

                    booksAdapter.notifyItemChanged(position)
                }
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun showToast(message: String) {
        val inflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.custom_toast_layout, findViewById(R.id.custom_toast_layout_root))
        val text: TextView = layout.findViewById(R.id.text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    override fun onResume() {
        super.onResume()
        booksAdapter.updateDataset(mutableBookItems)
    }

}