package com.pankaj.bookdex_capstone

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.pankaj.bookdex_capstone.R

class BooksAdapter(private val books: MutableList<BookItem>, private val listener: OnItemClickListener, private val databaseReference: DatabaseReference) :
    RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(book: BookItem, isFavourite: Boolean, position: Int)
    }

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewAuthor: TextView = view.findViewById(R.id.textViewAuthor)
        val imageViewThumbnail: ImageView = view.findViewById(R.id.imageViewThumbnail)

        val buttonFavourites: Button = view.findViewById(R.id.buttonFavourites)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        if (books.isEmpty()) {
            holder.textViewTitle.text = "No Results Found"
            holder.textViewAuthor.text = ""
            holder.imageViewThumbnail.visibility = View.GONE
            holder.buttonFavourites.visibility = View.GONE
        } else {
            val book = books[position].volumeInfo
            holder.textViewTitle.text = book.title
            holder.textViewAuthor.text = book.authors?.joinToString(", ") ?: "Author Unknown"
            holder.imageViewThumbnail.visibility = View.VISIBLE
            holder.buttonFavourites.visibility = View.VISIBLE
            Picasso.get()
                .load(book.imageLinks?.thumbnail)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imageViewThumbnail)

            val bookId = books[position].id

            databaseReference.child("favourites").child(bookId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isFavourite = snapshot.exists()
                    if (isFavourite) {
                        holder.buttonFavourites.text = "Remove from Favourites"
                        holder.buttonFavourites.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))

                    } else {
                        holder.buttonFavourites.text = "Add to Favourites"
                        holder.buttonFavourites.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimaryVariant))

                    }

                    holder.buttonFavourites.setOnClickListener {
                        listener.onItemClick(books[position], isFavourite, position)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            })
        }
    }


    override fun getItemCount() = if (books.isEmpty()) 1 else books.size

    fun updateDataset(newBooks: List<BookItem>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }
}
