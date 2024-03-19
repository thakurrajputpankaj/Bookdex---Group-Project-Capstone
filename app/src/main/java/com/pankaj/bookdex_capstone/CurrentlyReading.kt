package com.pankaj.bookdex_capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CurrentlyReading : AppCompatActivity() , ReadingAdapter.OnItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: ReadingAdapter
    private lateinit var databaseReferenceStats: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currently_reading)

        recyclerView = findViewById(R.id.books_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference = FirebaseDatabase.getInstance().reference.child("currentlyReading")
        databaseReferenceStats = FirebaseDatabase.getInstance().reference

        adapter = ReadingAdapter(mutableListOf(), this, databaseReference, databaseReferenceStats)


        recyclerView.adapter = adapter

        fetchBooksFromFirebase()
    }
    private fun fetchBooksFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookList = mutableListOf<Reading>()
                for (bookSnapshot in snapshot.children) {
                    val id = bookSnapshot.key ?: ""
                    val title = bookSnapshot.child("title").getValue(String::class.java) ?: ""
                    val author = bookSnapshot.child("author").getValue(String::class.java) ?: ""
                    val image = bookSnapshot.child("image").getValue(String::class.java) ?: ""
                    val pageCount = bookSnapshot.child("pageCount").getValue(Long::class.java) ?: 0
                    val pagesRead = bookSnapshot.child("pagesRead").getValue(Long::class.java) ?: 0

                    val readings = Reading(id, title, author, image,pageCount,pagesRead)
                    bookList.add(readings)
                }

                adapter.updateBooks(bookList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        private const val TAG = "ReadingActivity"
    }


    override fun onItemClick(readings: Reading) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Remove Book")
            .setMessage("Are you sure you want to remove this book?")
            .setPositiveButton("Yes") { dialog, _ ->
                val readngId = readings.id
                databaseReference.child(readngId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Book removed from Currently Reading Section", Toast.LENGTH_SHORT).show()
                        adapter.removeItemById(readngId)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error removing book from Currently Reading Section", exception)
                        Toast.makeText(this, "Failed to remove book from Currently Reading Section", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onItemReadComplete(readings: Reading) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Read the Complete Book ?")
            .setMessage("This Book will be removed for this section")
            .setPositiveButton("Yes") { dialog, _ ->
                val readngId = readings.id
                databaseReference.child(readngId).removeValue()
                    .addOnSuccessListener {
                        //Toast.makeText(this, "Book removed from Currently Reading Section", Toast.LENGTH_SHORT).show()
                        adapter.removeItemById(readngId)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error removing book from Currently Reading Section", exception)
                        //Toast.makeText(this, "Failed to remove book from Currently Reading Section", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }


}