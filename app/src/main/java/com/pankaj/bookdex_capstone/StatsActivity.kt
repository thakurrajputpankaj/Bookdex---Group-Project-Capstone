package com.pankaj.bookdex_capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatsActivity : AppCompatActivity() {

    private lateinit var textViewFavoriteBooks: TextView
    private lateinit var textViewTotalPagesRead: TextView
    private lateinit var textViewTotalBooksRead: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        textViewFavoriteBooks = findViewById(R.id.textViewFavoriteBooks)
        textViewTotalPagesRead = findViewById(R.id.textViewTotalPagesRead)
        textViewTotalBooksRead = findViewById(R.id.textViewTotalBooksRead)

        databaseReference = FirebaseDatabase.getInstance().reference.child("stats")


        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val stats = dataSnapshot.getValue(Stats::class.java)

                    textViewFavoriteBooks.text = "${stats?.favourites ?: 0}"
                    textViewTotalPagesRead.text = "${stats?.totalPages ?: 0}"
                    textViewTotalBooksRead.text = "${stats?.totalBooks ?: 0}"
                } else {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}

data class Stats(
    val favourites: Int = 0,
    val totalBooks: Int = 0,
    val totalPages: Int = 0
)
