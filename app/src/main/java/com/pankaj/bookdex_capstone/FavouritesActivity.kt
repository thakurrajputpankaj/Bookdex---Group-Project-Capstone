package com.pankaj.bookdex_capstone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouritesActivity : AppCompatActivity() , FavouritesAdapter.OnItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: FavouritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        recyclerView = findViewById(R.id.books_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference = FirebaseDatabase.getInstance().reference.child("favourites")

        adapter = FavouritesAdapter(mutableListOf(), this, this)

        recyclerView.adapter = adapter

        fetchBooksFromFirebase()
    }

    private fun fetchBooksFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookList = mutableListOf<Favourite>()
                for (bookSnapshot in snapshot.children) {
                    val id = bookSnapshot.key ?: ""
                    val title = bookSnapshot.child("title").getValue(String::class.java) ?: ""
                    val author = bookSnapshot.child("author").getValue(String::class.java) ?: ""
                    val image = bookSnapshot.child("image").getValue(String::class.java) ?: ""

                    val favourite = Favourite(id, title, author, image)
                    bookList.add(favourite)
                }

                adapter.updateBooks(bookList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        private const val TAG = "FavouritesActivity"
    }

    override fun onItemClick(favourite: Favourite) {
        val favouriteId = favourite.id
        databaseReference.child(favouriteId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Book removed from favourites", Toast.LENGTH_SHORT).show()
                adapter.removeItemById(favouriteId)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error removing book from favourites", exception)
                Toast.makeText(this, "Failed to remove book from favourites", Toast.LENGTH_SHORT).show()
            }
    }

}
