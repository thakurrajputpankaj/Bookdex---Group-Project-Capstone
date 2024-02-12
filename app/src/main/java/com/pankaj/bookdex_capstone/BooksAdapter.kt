package com.pankaj.bookdex_capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.pankaj.bookdex_capstone.R

class BooksAdapter(private val books: List<BookItem>) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewAuthor: TextView = view.findViewById(R.id.textViewAuthor)
        val imageViewThumbnail: ImageView = view.findViewById(R.id.imageViewThumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position].volumeInfo
        holder.textViewTitle.text = book.title
        holder.textViewAuthor.text = book.authors?.joinToString(", ") ?: "Author Unknown"
        Picasso.get()
            .load(book.imageLinks?.thumbnail)
            .placeholder(R.drawable.placeholder) // Ensure you have this placeholder in your drawable resources.
            .error(R.drawable.placeholder) // Ensure you have this error image in your drawable resources.
            .into(holder.imageViewThumbnail)
    }

    override fun getItemCount() = books.size
}
