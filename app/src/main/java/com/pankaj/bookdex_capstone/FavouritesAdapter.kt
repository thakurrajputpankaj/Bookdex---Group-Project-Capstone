package com.pankaj.bookdex_capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.pankaj.bookdex_capstone.R

class FavouritesAdapter(private var favourites: MutableList<Favourite>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(favourite: Favourite)
    }

    inner class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewAuthor: TextView = view.findViewById(R.id.textViewAuthor)
        val imageViewThumbnail: ImageView = view.findViewById(R.id.imageViewThumbnail)
        val buttonRemove: Button = view.findViewById(R.id.buttonRemove)

        init {
            buttonRemove.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val favourite = favourites[position]
                    itemClickListener.onItemClick(favourite)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item_fav, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val favourite = favourites[position]
        holder.textViewTitle.text = favourite.title
        holder.textViewAuthor.text = favourite.author

        Picasso.get()
            .load(favourite.image)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.imageViewThumbnail)
    }

    override fun getItemCount() = favourites.size

    fun updateBooks(newBooks: List<Favourite>) {
        favourites = newBooks.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItemById(id: String) {
        val index = favourites.indexOfFirst { it.id == id }
        if (index != -1) {
            favourites.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
