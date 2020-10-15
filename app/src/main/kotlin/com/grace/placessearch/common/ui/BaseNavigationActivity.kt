package com.grace.placessearch.common.ui

import android.content.Context
import android.content.Intent
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.grace.placessearch.BuildConfig
import com.grace.placessearch.R
import com.grace.placessearch.common.PlacesSearchConstants
import com.grace.placessearch.common.app.PlacesSearchApplication
import com.grace.placessearch.common.data.model.Venue
import com.grace.placessearch.common.ui.injection.component.ActivityComponent
import com.grace.placessearch.common.ui.injection.component.DaggerActivityComponent
import kotlinx.android.synthetic.main.custom_toast.view.*
import kotlinx.android.synthetic.main.search_toolbar.*

/**
 * To implement dynamic installation of feature modules using the play core library, see:
 *
 * https://developer.android.com/guide/playcore/play-feature-delivery
 *
 * https://github.com/android/app-bundle-samples/tree/master/DynamicFeatures
 *
 */
abstract class BaseNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .placesSearchComponent((application as PlacesSearchApplication).component())
                .build()
    }

    override fun onBackPressed() {
        when (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            true -> getDrawerLayout().closeDrawer(GravityCompat.START)
            false -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home, R.id.nav_settings, R.id.nav_favorites, R.id.nav_explore -> {
                getDrawerLayout().openDrawer(GravityCompat.START)  // OPEN DRAWER
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        getDrawerLayout().closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_favorites, R.id.nav_explore, R.id.nav_settings -> {
                displayCustomToast("Under construction. Coming soon!")
                return false
            }
        }
        return true
    }

    open fun component(): ActivityComponent {
        return activityComponent
    }

    protected fun setupDrawerListeners() {
        val toggle = ActionBarDrawerToggle(
                this, getDrawerLayout(), toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        getDrawerLayout().addDrawerListener(toggle)
        getDrawerLayout().addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                hideKeyboard()
            }

            override fun onDrawerOpened(drawerView: View) {
                hideKeyboard()
            }

            override fun onDrawerClosed(drawerView: View) {
                hideKeyboard()
            }

            override fun onDrawerStateChanged(newState: Int) {
                hideKeyboard()
            }
        })

        toggle.syncState()
    }

    protected open fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.hamburger)
    }

    protected fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = currentFocus
        if (focusedView != null) {
            imm.toggleSoftInputFromWindow(this.currentFocus!!.windowToken,
                    InputMethodManager.SHOW_FORCED, 0)
        }
    }

    protected fun setupNavigationView() {
        val menu = getNavigationView().menu
        if (menu.getItem(1) != null) {
            menu.getItem(1).isChecked = true
        }
    }

    protected fun hideKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val focusedView = currentFocus
        if (focusedView != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
    }

    protected fun displayCustomToast(message: String) {
        val layout = layoutInflater.inflate(R.layout.custom_toast,
                findViewById<LinearLayout>(R.id.custom_toast_container) as? ViewGroup) ?: return

        layout.text_view.text = message

        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    protected fun goToVenueDetailActivity(venue: Venue) {
        Intent().setClassName(BuildConfig.APPLICATION_ID, VENUE_DETAIL_ACTIVITY_CLASSNAME)
                .putExtra(PlacesSearchConstants.VENUE_NAME_EXTRA, venue.name)
                .putExtra(PlacesSearchConstants.VENUE_ID_EXTRA, venue.id)
                .also {
                    startActivity(it)
                }

        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)

//        val intent = Intent(this, VENUE_DETAIL_ACTIVITY_CLASSNAME)
//        intent.putExtra(PlacesSearchConstants.VENUE_NAME_EXTRA, venue.name)
//        intent.putExtra(PlacesSearchConstants.VENUE_ID_EXTRA, venue.id)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    abstract fun getNavigationView(): NavigationView

    abstract fun getDrawerLayout(): DrawerLayout

    companion object {
        private const val PACKAGE_NAME = "com.google.android.samples.dynamicfeatures.ondemand"

        private const val VENUE_DETAIL_PACKAGE_NAME = "com.grace.placessearch.details.ui"
        
        private const val VENUE_DETAIL_ACTIVITY_CLASSNAME = "$VENUE_DETAIL_PACKAGE_NAME.VenueDetailsActivity"

    }
}