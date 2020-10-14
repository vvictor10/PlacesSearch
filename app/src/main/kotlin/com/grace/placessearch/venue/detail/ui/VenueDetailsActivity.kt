package com.grace.placessearch.venue.detail.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import android.util.LruCache
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.grace.placessearch.R
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager
import com.grace.placessearch.common.data.model.Category
import com.grace.placessearch.common.data.model.Venue
import com.grace.placessearch.common.ui.BaseNavigationActivity
import com.grace.placessearch.common.ui.view.ViewUtils
import com.grace.placessearch.common.util.PlacesSearchUtil
import com.grace.placessearch.search.ui.VenuesContract
import com.grace.placessearch.search.ui.VenuesPresenter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_venue_details.*
import kotlinx.android.synthetic.main.content_venue_details.*
import timber.log.Timber
import javax.inject.Inject

class VenueDetailsActivity : BaseNavigationActivity(), VenuesContract.View {

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var mVenuesPresenter: VenuesPresenter

    @Inject
    lateinit var preferenceManager: PlacesSearchPreferenceManager

    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_details)
        ButterKnife.bind(this)

        component().inject(this)

        setupNavigationView()

        setupDrawerListeners()

        initToolbar(toolbar)

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        mVenuesPresenter.bindView(this)

        val displayMetrics = resources.displayMetrics
        imageWidth = displayMetrics.widthPixels
        imageHeight = (imageWidth * 1.0).toInt() //aspect ratio
        setViewHeight(map_image, imageHeight)

        val venueId = intent.getStringExtra(PlacesSearchConstants.VENUE_ID_EXTRA)

        loading_indicator.visibility = View.VISIBLE
        mVenuesPresenter.doGetVenue(venueId)

    }

    override fun onResume() {
        super.onResume()
        @Suppress("UNCHECKED_CAST")
        val cached = lruCache.get(PlacesSearchConstants.CACHE_KEY_TRENDING_VENUES) as? List<String>
        if (cached != null) {
            Timber.d("No. of Trending venues: %d", cached.size)
        }
    }

    override fun onPause() {
        mVenuesPresenter.unBindView()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onVenue(venue: Venue) {
        toolbar_title.text = venue.name

        loadMapImage(map_image, venue)
        setupVenueUrl(venue)

        if (!venue.categories.isEmpty()) {
            venue_categories.text = getCategoriesString(venue.categories)
        } else {
            venue_categories.visibility = View.GONE
        }

        setupAddressInfo(venue)
        setInitialFavoriteStatus(preferenceManager, venue)
    }

    override fun onError() {
        loading_indicator.visibility = View.INVISIBLE
        primary_details_card_view.visibility = View.INVISIBLE
        address_card_view.visibility = View.INVISIBLE
        about_card_view.visibility = View.INVISIBLE
        displayCustomToast("Something went wrong. Please try later.")
    }

    override fun onSearch(venues: java.util.ArrayList<Venue>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSuggestedSearches(suggestedSearches: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    public override fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.appbar_back_white)
    }

    private fun setViewHeight(view: View, viewHeight: Int) {
        val vto = view.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                if (view.height != viewHeight) {
                    view.layoutParams.height = viewHeight
                    view.requestLayout()
                }
                return true
            }
        })
    }

    private fun setInitialFavoriteStatus(placesPreferenceManager: PlacesSearchPreferenceManager?, venue: Venue) {

        if (placesPreferenceManager == null) {
            return
        }

        isFavorite = PlacesSearchUtil.isFavorite(placesPreferenceManager, venue.id)
        if (isFavorite) {
            animateFavoriteStatus(true, favorite_status, non_favorite_status)
        } else {
            animateFavoriteStatus(true, non_favorite_status, favorite_status)
        }

        val onClickListener = View.OnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                PlacesSearchUtil.addFavorite(placesPreferenceManager, venue.id)
                animateFavoriteStatus(false, favorite_status, non_favorite_status)
            } else {
                PlacesSearchUtil.removeFavorite(placesPreferenceManager, venue.id)
                animateFavoriteStatus(false, non_favorite_status, favorite_status)
            }
        }

        non_favorite_status.setOnClickListener(onClickListener)
        favorite_status.setOnClickListener(onClickListener)
    }

    private fun setupAddressInfo(venue: Venue) {
        if (venue.location != null && venue.location?.formattedAddress!!.isNotEmpty()) {
            for (i in 0 until venue.location?.formattedAddress!!.size) {
                val formattedAddress = venue.location!!.formattedAddress[i]
                when (i) {
                    0 -> addr_line_1.text = formattedAddress
                    1 -> addr_line_2.text = formattedAddress
                    2 -> addr_line_3.text = formattedAddress
                }
            }
            directions_image.setOnClickListener { launchMapsForDirections(venue) }
        } else {
            address_card_view.visibility = View.GONE
        }
    }

    private fun loadMapImage(imageView: ImageView, venue: Venue) {

        if (venue.location == null) {
            return
        }

        val imageUrl = getMapImageUrl(venue)

        picasso.load(imageUrl).into(imageView, object : Callback {
            override fun onSuccess() {
                Timber.d("Image loaded for url %s", imageUrl)
                loading_indicator.visibility = View.GONE
            }

            override fun onError() {
                Timber.w("Failed to load map image")
            }
        })
    }

    private fun getMapImageUrl(venue: Venue): String {
        val staticMapImageUrlBuilder = StringBuilder()
        staticMapImageUrlBuilder.append("https://maps.googleapis.com/maps/api/staticmap?size=")
        staticMapImageUrlBuilder.append(imageWidth.toString() + "x" + imageHeight + "&maptype=roadmap&markers=color:red%7Clabel:A%7C")
        staticMapImageUrlBuilder.append(PlacesSearchConstants.USER_LOCATION_LAT.toString() + "," + PlacesSearchConstants.USER_LOCATION_LNG)
        staticMapImageUrlBuilder.append("&markers=color:red%7Clabel:B%7C" + String.format("%.4f", venue.location?.lat))
        staticMapImageUrlBuilder.append("," + String.format("%.4f", venue.location?.lng))
        staticMapImageUrlBuilder.append("&key=AIzaSyBJ8FpHurNJQ0oyEhxY4U1HMAZ2xF_pv9w")

        return staticMapImageUrlBuilder.toString()
    }

    private fun setupVenueUrl(venue: Venue) {
        if (venue.url != null) {
            venue_url.text = venue.url
            venue_url.paintFlags = venue_url.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            venue_url.setOnClickListener { launchUrl(venue.url!!) }

            //.setOnClickListener(View.OnClickListener { launchUrl(venue.url) })
        } else {
            venue_url.visibility = View.GONE
        }
    }

    private fun launchMapsForDirections(venue: Venue) {
        val staticMapImageUrlBuilder = StringBuilder()
        staticMapImageUrlBuilder.append("https://www.google.com/maps/dir/?api=1&travelmode=driving&origin=")
        staticMapImageUrlBuilder.append(PlacesSearchConstants.USER_LOCATION_LAT.toString() + "," + PlacesSearchConstants.USER_LOCATION_LNG)
        staticMapImageUrlBuilder.append("&destination=" + String.format("%.4f", venue.location?.lat))
        staticMapImageUrlBuilder.append("," + String.format("%.4f", venue.location?.lng))
        staticMapImageUrlBuilder.append("&key=AIzaSyBJ8FpHurNJQ0oyEhxY4U1HMAZ2xF_pv9w")

        val mapUrl = staticMapImageUrlBuilder.toString()

        launchUrl(mapUrl)
    }

    protected fun launchUrl(urlToLoad: String) {

        if (ViewUtils.isChromeTabSupported(this)) { // Chrome Custom tab supported

            Timber.d("Launching using Chrome custom tab.")

            // Launch in Chrome CustomTabs
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            // set toolbar color
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.primary_dark))
            builder.setShowTitle(true)
            customTabsIntent.launchUrl(this, Uri.parse(urlToLoad))

        } else { // Chrome Custom tabs not supported

            Timber.d("Launching using browser as Chrome custom tab is not supported.")

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToLoad))
            browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(browserIntent)
        }
    }

    private fun getCategoriesString(categories: List<Category>): String {
        if (categories.isEmpty()) {
            return ""
        }

        val categoriesString = StringBuilder()
        for (i in categories.indices) {
            categoriesString.append(categories[i].name)
            if (i != categories.size - 1) {
                categoriesString.append(", ")
            }
        }

        return categoriesString.toString()
    }

    private fun animateFavoriteStatus(isInitAnimation: Boolean, viewToDisplay: View, viewToHide: View) {

        if (viewToHide.visibility != View.VISIBLE) {
            return
        }

        // Skip scale down animation for nonFavoriteStatusImage.
        if (viewToHide === favorite_status) {

            // Animate the 'view-to-hide' to 0% opacity. After the animation ends,
            // set its visibility to GONE as an optimization step (it won't
            // participate in layout passes, etc.)
            viewToHide.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(PlacesSearchConstants.HEART_CROSS_FADE_ANIMATION_DURATION.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            viewToHide.visibility = View.GONE
                        }
                    })
        }

        // Set the 'view-to-display' to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewToDisplay.scaleX = 0f
        viewToDisplay.scaleY = 0f
        viewToDisplay.visibility = View.VISIBLE

        // Animate the 'view-to-display' to 100% opacity, and clear any animation
        // listener set on the view.

        // If displaying 'non-favorite' status or initial display of 'favorite'
        // status - animate alpha value instantly
        var duration = PlacesSearchConstants.HEART_CROSS_FADE_ANIMATION_DURATION
        if (viewToDisplay === non_favorite_status || isInitAnimation) {
            duration = 0
        }

        viewToDisplay.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration.toLong())
                .setListener(null)
    }
}