package com.grace.placessearch.search.ui

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grace.placessearch.R
import com.grace.placessearch.common.ui.view.ViewUtils
import kotlinx.android.synthetic.main.suggested_search_header.view.*
import kotlinx.android.synthetic.main.suggested_search_item.view.*
import timber.log.Timber

class SuggestedSearchResultsAdapter(private val recyclerView: RecyclerView, private var suggestedSearchResults: MutableList<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 0
        const val ITEM_TYPE_DATA = 1
    }

    lateinit var suggestedSearchItemClickListener: SuggestedSearchOnClickListener
    lateinit var backgroundTouchListener: View.OnTouchListener
    var searchInput: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            ITEM_TYPE_HEADER -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.suggested_search_header, parent, false)
                return SuggestedSearchHeaderViewHolder(view)
            }

            ITEM_TYPE_DATA -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.suggested_search_item, parent, false)
                val suggestedSearchItemViewHolder = SuggestedSearchItemViewHolder(view)

                // text click listener
                suggestedSearchItemViewHolder.itemView.setOnClickListener {
                    val adapterPos = suggestedSearchItemViewHolder.adapterPosition
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        suggestedSearchItemClickListener.onSuggestedSearchItemClick(suggestedSearchResults[adapterPos])
                    }
                }

                return suggestedSearchItemViewHolder
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.suggested_search_item, parent, false)
                val suggestedSearchItemViewHolder = SuggestedSearchItemViewHolder(view)
                suggestedSearchItemViewHolder.itemView.setOnClickListener {
                    val adapterPos = suggestedSearchItemViewHolder.adapterPosition
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        suggestedSearchItemClickListener.onSuggestedSearchItemClick(suggestedSearchResults[adapterPos])
                    }
                }
                return suggestedSearchItemViewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return suggestedSearchResults.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SuggestedSearchItemViewHolder) {
            boldAndSetText(holder)
            setAnimation(holder.itemView, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_TYPE_DATA
    }

    fun setClickListeners(listener: SuggestedSearchOnClickListener, backgroundTouchListener: View.OnTouchListener) {
        this.suggestedSearchItemClickListener = listener
        this.backgroundTouchListener = backgroundTouchListener
    }

    fun clearSearchResults() {
        suggestedSearchResults.clear()
        notifyDataSetChanged()
    }

    fun isSearchResultsEmpty(): Boolean {
        return suggestedSearchResults.isEmpty()
    }

    fun setSearchResults(newSearchResults: List<String>?, newSearchInput: String) {

        // update data
        // 1) if newSearchInput is substring of searchInput AND new list size is < current list size, this means a search refine
        // 2) else, do refresh of entire list

        try {
            // refine list
            if (newSearchInput != null && searchInput != null &&
                    newSearchInput.contains(searchInput) && // current searchInput is a substring of newSearchInput
                    newSearchResults!!.size < suggestedSearchResults.size) {

                this.searchInput = newSearchInput

                var i: Int
                do {
                    i = 0
                    while (i < suggestedSearchResults.size) {
                        val searchTerm = suggestedSearchResults[i]
                        if (!newSearchResults.contains(searchTerm)) { // if term is not in new results, remove
                            suggestedSearchResults.removeAt(i)
                            notifyItemRemoved(i)
                            break
                        } else {
                            boldAndSetText(recyclerView.findViewHolderForAdapterPosition(i))
                        }
                        i++
                    }
                } while (i < suggestedSearchResults.size)

                // add newSearchResults to end of suggestedSearchResults list
                i = 0
                while (i < newSearchResults.size) {
                    if (!suggestedSearchResults.contains(newSearchResults[i])) {
                        suggestedSearchResults.add(newSearchResults[i])
                        notifyItemInserted(suggestedSearchResults.size - 1)
                    }
                    i++
                }

            } else { // refresh list

                this.searchInput = newSearchInput

                suggestedSearchResults.clear()
                if (newSearchResults != null) {
                    suggestedSearchResults.addAll(newSearchResults)
                }
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Timber.w(e)
        }


    }

    private fun boldAndSetText(viewHolder: RecyclerView.ViewHolder?) {
        if (viewHolder == null || viewHolder !is SuggestedSearchItemViewHolder) {
            return
        }

        val adapterPos = viewHolder.adapterPosition
        if (adapterPos != RecyclerView.NO_POSITION) {
            val suggestedSearch = suggestedSearchResults[adapterPos]
            val spannableStringBuilder = ViewUtils.applyBoldStyleToText(suggestedSearch, searchInput)
            viewHolder.suggestedSearchItemTextView.text = spannableStringBuilder
        }
    }

    private fun setAnimation(view: View, position: Int) {
        if (position != 0) {
            startAnimations(view, position)
        }
    }

    private fun startAnimations(view: View, position: Int) {

        var factor = position.toFloat() / 10
        if (factor == 0f) {
            factor = .05f
        }

        val startDelay = position * 100
        val duration = 600

        // alpha
        view.alpha = 0f
        val anim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        anim.duration = duration.toLong()
        anim.startDelay = startDelay.toLong()

        // translate
        val anim1 = ObjectAnimator.ofFloat(view, "translationY", 200f, 0f)
        anim1.duration = duration.toLong()
        anim1.interpolator = AccelerateInterpolator(factor)

        anim.start()
        anim1.start()
    }

    class SuggestedSearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val suggestedSearchItemTextView: TextView = itemView.suggested_search_item_text
    }

    class SuggestedSearchHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val suggestedSearchHeaderText: TextView = itemView.suggested_search_header_text
    }

    interface SuggestedSearchOnClickListener {
        fun onSuggestedSearchItemClick(item: String)
    }

}