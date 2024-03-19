package com.pankaj.bookdex_capstone
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot

class StatsManager(private val databaseReference: DatabaseReference) {

    fun updateFavouritesCount(updateFunction: (currentCount: Long) -> Long): Task<Void> {
        val favouritesRef = databaseReference.child("stats").child("favourites")

        return favouritesRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val currentCount = task.result?.value as? Long ?: 0
                val newCount = updateFunction(currentCount)
                favouritesRef.setValue(newCount).addOnSuccessListener {
                    // Update successful
                }.addOnFailureListener { exception ->
                    throw exception
                }
            } else {
                throw task.exception ?: Exception("Failed to fetch favourites count")
            }
            return@continueWith null
        }
    }

    fun updateTotalBooksCount(updateFunction: (currentCount: Long) -> Long): Task<Void> {
        val totalBooksRef = databaseReference.child("stats").child("totalBooks")

        return totalBooksRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val currentCount = task.result?.value as? Long ?: 0
                val newCount = updateFunction(currentCount)
                totalBooksRef.setValue(newCount).addOnSuccessListener {
                    // Update successful
                }.addOnFailureListener { exception ->
                    throw exception
                }
            } else {
                throw task.exception ?: Exception("Failed to fetch total books count")
            }
            return@continueWith null
        }
    }

    fun updateTotalPagesCount(updateFunction: (currentCount: Long) -> Long): Task<Void> {
        val totalPagesRef = databaseReference.child("stats").child("totalPages")

        return totalPagesRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val currentCount = task.result?.value as? Long ?: 0
                val newCount = updateFunction(currentCount)
                totalPagesRef.setValue(newCount).addOnSuccessListener {
                    // Update successful
                }.addOnFailureListener { exception ->
                    throw exception
                }
            } else {
                throw task.exception ?: Exception("Failed to fetch total pages count")
            }
            return@continueWith null
        }
    }
}
