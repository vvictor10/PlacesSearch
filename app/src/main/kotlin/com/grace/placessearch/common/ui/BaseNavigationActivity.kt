package com.grace.placessearch.common.ui

import android.content.Context
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import com.grace.placessearch.R
import com.grace.placessearch.common.app.PlacesSearchApplication
import com.grace.placessearch.common.ui.injection.component.ActivityComponent
import com.grace.placessearch.common.ui.injection.component.DaggerActivityComponent
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_toast.view.*
import kotlinx.android.synthetic.main.search_toolbar.*

abstract class BaseNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .placesSearchComponent((application as PlacesSearchApplication).component())
                .build()
    }

    override fun onBackPressed() {
        when (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            true -> drawer_layout.closeDrawer(GravityCompat.START)
            false -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home, R.id.nav_settings, R.id.nav_favorites, R.id.nav_explore -> {
                drawer_layout.openDrawer(GravityCompat.START)  // OPEN DRAWER
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        drawer_layout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_favorites, R.id.nav_explore, R.id.nav_settings -> {
                displayCustomToast("Under construction. Coming soon!")
                return false
            }
        }
        return true
    }

    fun component(): ActivityComponent {
        return activityComponent
    }

    protected fun setupDrawerListeners() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
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
        val menu = nav_view.menu
        if (menu?.getItem(1) != null) {
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
}