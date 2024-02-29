package com.pankaj.bookdex_capstone

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.pankaj.bookdex_capstone.R

class BooksAdapter(private val books: List<BookItem>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(book: BookItem)
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
            Picasso.get()
                .load(book.imageLinks?.thumbnail)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imageViewThumbnail)

            holder.buttonFavourites.setOnClickListener{
                listener.onItemClick(books[position])
            }
        }
    }

    override fun getItemCount() = if (books.isEmpty()) 1 else books.size
}
