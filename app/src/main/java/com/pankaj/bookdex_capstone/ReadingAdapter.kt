package com.pankaj.bookdex_capstone

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import com.pankaj.bookdex_capstone.R

class ReadingAdapter(
    private var readings: MutableList<Reading>,
    private val itemClickListener: OnItemClickListener,
    private val databaseReference: DatabaseReference,
    private val databaseReferenceStats: DatabaseReference,
) :
    RecyclerView.Adapter<ReadingAdapter.ReadingViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(readings: Reading)
    }

    inner class ReadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewAuthor: TextView = view.findViewById(R.id.textViewAuthor)
        val imageViewThumbnail: ImageView = view.findViewById(R.id.imageViewThumbnail)
        val buttonSave: Button = view.findViewById(R.id.buttonSave)
        val buttonRemove: Button = view.findViewById(R.id.buttonRemove)
        val rangeSlider: RangeSlider = view.findViewById(R.id.rangeSlider)
        val pagesCount: TextView = view.findViewById(R.id.pagesCount)
        val pagesRead: TextView = view.findViewById(R.id.pagesRead)
        init {

            rangeSlider.stepSize = 1f
            rangeSlider.addOnChangeListener { slider, value, fromUser ->
                val roundedValue = value.toInt()
                slider.values = listOf(roundedValue.toFloat())
            }


            buttonRemove.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val favourite = readings[position]
                    itemClickListener.onItemClick(favourite )
                }
            }




            buttonSave.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val reading = readings[position]
                    val newProgress = rangeSlider.values[0].toInt()

                    databaseReference.child(reading.id).child("pagesRead").get().addOnSuccessListener { dataSnapshot ->
                        val currentPagesRead = dataSnapshot.value as? Long ?: 0

                        pagesRead.text = "Pages Read: ${newProgress}"

                        val newPagesRead =  newProgress - currentPagesRead

                        databaseReference.child(reading.id).child("pagesRead").setValue(newProgress)
                            .addOnSuccessListener {
                                Toast.makeText(itemView.context, "Progress saved successfully", Toast.LENGTH_SHORT).show()

                                databaseReferenceStats.child("stats").child("totalPages").get().addOnSuccessListener { totalDataSnapshot ->
                                    val currentTotalPages = totalDataSnapshot.value as? Long ?: 0
                                    val newTotalPages = currentTotalPages + newPagesRead

                                    databaseReferenceStats.child("stats").child("totalPages").setValue(newTotalPages)
                                        .addOnSuccessListener {
                                        }
                                        .addOnFailureListener { totalException ->
                                            Log.e(TAG, "Error updating total pages count", totalException)
                                        }
                                }.addOnFailureListener { totalException ->
                                    Log.e(TAG, "Error retrieving total pages count", totalException)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Error saving progress", exception)
                                Toast.makeText(itemView.context, "Failed to save progress", Toast.LENGTH_SHORT).show()
                            }
                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "Error retrieving pagesRead count", exception)
                        Toast.makeText(itemView.context, "Failed to save progress", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item_reading, parent, false)
        return ReadingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReadingViewHolder, position: Int) {
        val reading_ = readings[position]
        holder.textViewTitle.text = reading_.title
        holder.textViewAuthor.text = reading_.author

        holder.pagesCount.text = "Total Pages: ${reading_.pageCount}"
        holder.pagesRead.text = "Pages Read: ${reading_.pagesRead}"

        holder.rangeSlider.valueFrom = 0f
        holder.rangeSlider.valueTo = reading_.pageCount.toFloat()
        holder.rangeSlider.values = listOf(reading_.pagesRead.toFloat())
        holder.buttonRemove.setBackgroundResource(R.color.red)
        Picasso.get()
            .load(reading_.image)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.imageViewThumbnail)
    }

    override fun getItemCount() = readings.size

    fun updateBooks(newBooks: List<Reading>) {
        readings = newBooks.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItemById(id: String) {
        val index = readings.indexOfFirst { it.id == id }
        if (index != -1) {
            readings.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
