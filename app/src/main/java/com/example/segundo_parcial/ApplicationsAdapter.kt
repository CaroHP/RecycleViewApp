package com.example.segundo_parcial

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.FieldPosition
import java.util.ArrayList

class ApplicationsAdapter(contex: Context, feedEntries: ArrayList<FeedEntry>) : RecyclerView.Adapter<ApplicationsAdapter.ViewHolder>() {

    private var localContext: Context ? = null
    private var localFeedEntries: ArrayList<FeedEntry> ? = null

    init {
        localContext = contex
        localFeedEntries = feedEntries
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(localContext)
        val view: View = layoutInflater.inflate(R.layout.row_item, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationsAdapter.ViewHolder, position: Int){
        val currentFeed: FeedEntry = localFeedEntries!![position]
        holder.textName.text = currentFeed.name
        holder.textArtist.text = currentFeed.artist
        holder.textDescription.text = currentFeed.summary.take(250).plus("...")
    }

    override fun getItemCount():Int {
        return localFeedEntries?.size ?: 0
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val textName: TextView = v.findViewById(R.id.textViewName)
        val textArtist: TextView = v.findViewById(R.id.textViewArtist)
        val textDescription: TextView = v.findViewById(R.id.textViewDescription)
    }

}