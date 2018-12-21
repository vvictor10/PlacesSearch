package com.grace.placessearch.search.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.grace.placessearch.R
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.util.PlacesSearchUtil
import com.grace.placessearch.data.model.Venue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_venue.view.*
import java.util.*

class SearchResultsAdapter(private val context: Context, private val listener: VenueListener,
                           private val placesPreferenceManager: PlacesSearchPreferenceManager,
                           private val picasso: Picasso) : RecyclerView.Adapter<SearchResultsAdapter.VenueViewHolder>() {

    private var data: List<Venue> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VenueViewHolder(inflater.inflate(R.layout.list_item_venue, parent, false))
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(context, data[position], listener, picasso, placesPreferenceManager)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(batches: List<Venue>) {
        this.data = ArrayList(batches)
        notifyDataSetChanged()
    }

    interface VenueListener {
        fun onVenueItemClicked(venue: Venue)
    }

    class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val venueName: TextView = itemView.venue_name
        private val venueContainer: RelativeLayout = itemView.venue_container
        private val venueCategoryImage: ImageView = itemView.venue_category_image
        private val categoryName: TextView = itemView.category_name
        private val distanceToUserLocation: TextView = itemView.distance_to_user_location
        private val favoriteStatusImage: ImageView = itemView.favorite_status
        private val nonFavoriteStatusImage: ImageView = itemView.non_favorite_status

        private var isFavorite: Boolean = false
        private var favoriteStatusNeedsUpdating: Boolean = false
        private var venueId: String? = null

        init {
            isFavorite = false
        }

        private fun loadImage(context: Context, imageView: ImageView, venue: Venue, picasso: Picasso) {

            if (venue.categories.isEmpty() || venue.categories[0].icon == null) {
                return
            }

            val imageUrl = venue.listImgUrl

            if (imageUrl == null) {
                imageView.setImageDrawable(context.getDrawable(android.R.drawable.stat_notify_error))
                return
            }

            picasso.load(imageUrl).into(imageView)
        }

        fun bind(context: Context, data: Venue, listener: VenueListener, picasso: Picasso, placesPreferenceManager: PlacesSearchPreferenceManager) {

            isFavorite = false

            // Venue name
            venueName.text = data.name

            // Category
            when (data.categories.size >= 1) {
                true -> categoryName.text = data.categories[0].name
                false -> categoryName.text = context.getString(R.string.unavailable)
            }

            // distance
            val distanceToUserStr = PlacesSearchUtil.getDistanceInMilesToUserLocation(data.location)
            when (distanceToUserStr == null) {
                true -> distanceToUserLocation.text = context.getString(R.string.unavailable)
                false -> distanceToUserLocation.text = "$distanceToUserStr miles"
            }

            // load category image
            loadImage(context, venueCategoryImage, data, picasso)

            venueContainer.setOnClickListener {
                listener.onVenueItemClicked(data)
                favoriteStatusNeedsUpdating = true
                venueId = data.id
            }
            setInitialFavoriteStatus(placesPreferenceManager, data)

            itemView.viewTreeObserver.addOnWindowFocusChangeListener{ hasFocus ->
                if (hasFocus && favoriteStatusNeedsUpdating && venueId != null && venueId == data.id) {
                    refreshFavoriteStatus(data.id, placesPreferenceManager)
                    favoriteStatusNeedsUpdating = false
                    venueId = null
                }
            }
        }

        private fun refreshFavoriteStatus(venueId: String, placesPreferenceManager: PlacesSearchPreferenceManager) {
            val latestFavoriteStatus = PlacesSearchUtil.isFavorite(placesPreferenceManager, venueId)
            if (!isFavorite && latestFavoriteStatus) { // newly favorited
                isFavorite = latestFavoriteStatus
                animateFavoriteStatus(false, favoriteStatusImage, nonFavoriteStatusImage!!)
            } else if (isFavorite && !latestFavoriteStatus) { // newly un-favorited
                isFavorite = latestFavoriteStatus
                animateFavoriteStatus(false, nonFavoriteStatusImage, favoriteStatusImage!!)
            }
        }

        private fun setInitialFavoriteStatus(placesPreferenceManager: PlacesSearchPreferenceManager, venue: Venue) {
            isFavorite = PlacesSearchUtil.isFavorite(placesPreferenceManager, venue.id)
            if (isFavorite) {
                animateFavoriteStatus(true, favoriteStatusImage, nonFavoriteStatusImage!!)
            } else {
                animateFavoriteStatus(true, nonFavoriteStatusImage, favoriteStatusImage!!)
            }

            val onClickListener = View.OnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) {
                    PlacesSearchUtil.addFavorite(placesPreferenceManager, venue.id)
                    animateFavoriteStatus(false, favoriteStatusImage, nonFavoriteStatusImage!!)
                } else {
                    PlacesSearchUtil.removeFavorite(placesPreferenceManager, venue.id)
                    animateFavoriteStatus(false, nonFavoriteStatusImage, favoriteStatusImage!!)
                }
            }

            nonFavoriteStatusImage!!.setOnClickListener(onClickListener)
            favoriteStatusImage!!.setOnClickListener(onClickListener)
        }

        private fun animateFavoriteStatus(isInitAnimation: Boolean, viewToDisplay: View?, viewToHide: View) {

            if (viewToHide.visibility != View.VISIBLE) {
                return
            }

            // Skip scale down animation for nonFavoriteStatusImage.
            if (viewToHide === favoriteStatusImage) {

                // Animate the 'view-to-hide' to 0% opacity. After the animation ends,
                // set its visibility to GONE as an optimization step (it won't
                // participate in layout passes, etc.)
                viewToHide.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .setDuration(PlacesSearchConstants.HEART_CROSS_FADE_ANIMATION_DURATION.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                viewToHide.setVisibility(View.GONE)
                            }
                        })
            }

            // Set the 'view-to-display' to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            viewToDisplay!!.scaleX = 0f
            viewToDisplay.scaleY = 0f
            viewToDisplay.visibility = View.VISIBLE

            // Animate the 'view-to-display' to 100% opacity, and clear any animation
            // listener set on the view.

            // If displaying 'non-favorite' status or initial display of 'favorite'
            // status - animate alpha value instantly
            var duration = PlacesSearchConstants.HEART_CROSS_FADE_ANIMATION_DURATION
            if (viewToDisplay === nonFavoriteStatusImage || isInitAnimation) {
                duration = 0
            }

            viewToDisplay.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration.toLong())
                    .setListener(null)
        }

    }

}
