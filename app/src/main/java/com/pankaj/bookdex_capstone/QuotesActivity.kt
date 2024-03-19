package com.pankaj.bookdex_capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class QuotesActivity : AppCompatActivity() {

    private lateinit var quoteTextView: TextView
    private lateinit var getQuoteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)

        quoteTextView = findViewById(R.id.quoteTextView)
        getQuoteButton = findViewById(R.id.getQuoteButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.quotable.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(QuoteService::class.java)

        getQuoteButton.setOnClickListener {
            service.getRandomQuote().enqueue(object : Callback<Quote> {
                override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                    if (response.isSuccessful) {
                        val quote = response.body()
                        quote?.let {
                            quoteTextView.text = it.content
                        }
                    } else {
                        quoteTextView.text = "Failed to get quote"
                    }
                }

                override fun onFailure(call: Call<Quote>, t: Throwable) {
                    quoteTextView.text = "Error: ${t.message}"
                }
            })
        }
    }
}

data class Quote(
    val _id: String,
    val content: String,
    val author: String,
    val tags: List<String>,
    val authorSlug: String,
    val length: Int,
    val dateAdded: String,
    val dateModified: String
)



interface QuoteService {
    @GET("random")
    fun getRandomQuote(): Call<Quote>
}
