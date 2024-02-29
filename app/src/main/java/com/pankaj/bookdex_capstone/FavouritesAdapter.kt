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
import android.content.Context
import androidx.core.content.ContextCompat

class FavouritesAdapter(
    private var favourites: MutableList<Favourite>,
    private val itemClickListener: OnItemClickListener,
    private val context: Context
) :
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
                    showRemoveConfirmationDialog(favourite)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item_fav, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        if (favourites.isEmpty()) {
            holder.textViewTitle.text = "No Books to Show"
            holder.textViewAuthor.text = ""
            holder.imageViewThumbnail.visibility = View.GONE
            holder.buttonRemove.visibility = View.GONE
        } else {
            val favourite = favourites[position]
            holder.textViewTitle.text = favourite.title
            holder.textViewAuthor.text = favourite.author
            holder.imageViewThumbnail.visibility = View.VISIBLE
            holder.buttonRemove.visibility = View.VISIBLE
            holder.buttonRemove.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))

            Picasso.get()
                .load(favourite.image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imageViewThumbnail)
        }
    }

    override fun getItemCount() = if (favourites.isEmpty()) 1 else favourites.size

    fun updateBooks(newBooks: List<Favourite>) {
        favourites.clear()
        favourites.addAll(newBooks)
        notifyDataSetChanged()
    }

    fun removeItemById(id: String) {
        val index = favourites.indexOfFirst { it.id == id }
        if (index != -1) {
            favourites.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun showRemoveConfirmationDialog(favourite: Favourite) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle("Remove Item")
            setMessage("Are you sure you want to remove this item?")
            setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                removeItemById(favourite.id)
                itemClickListener.onItemClick(favourite)
            }
            setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            create().show()
        }
    }
}
