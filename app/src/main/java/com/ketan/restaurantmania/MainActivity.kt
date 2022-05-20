package com.ketan.restaurantmania

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import fragment.FavouriteFragment
import fragment.HistoryFragment
import fragment.HomeFragment
import fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var DrawerLayout : DrawerLayout
    lateinit var CoordinatorLayout : CoordinatorLayout
    lateinit var ToolBarLayout : androidx.appcompat.widget.Toolbar
    lateinit var AppBarLayout : AppBarLayout
    lateinit var FrameLayout : FrameLayout
    lateinit var navigationView : NavigationView
    var previous : MenuItem? = null
    lateinit var sharedpreferences: SharedPreferences
    lateinit var hview : View
    lateinit var searchbar : EditText
    lateinit var txtHeaderName : TextView
    lateinit var txtHeaderEmail : TextView
    lateinit var sharedPreferences_profie: SharedPreferences
    var name : String? ="Name"
    var email : String? ="email"



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DrawerLayout = findViewById(R.id.DrawerLayout)
        CoordinatorLayout = findViewById(R.id.CoordinatorLayout)
        ToolBarLayout = findViewById(R.id.ToolBarLayout)
        AppBarLayout = findViewById(R.id.AppBarLayout)
        FrameLayout = findViewById(R.id.FrameLayout)
        navigationView = findViewById(R.id.navigationView)
        sharedpreferences =  getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)
        hview = navigationView.getHeaderView(0)
        txtHeaderName = hview.findViewById(R.id.txtHeaderName)
        txtHeaderEmail = hview.findViewById(R.id.txtHeaderEmail)
        sharedPreferences_profie = getSharedPreferences(getString(R.string.ProfilePref), Context.MODE_PRIVATE)
        setuptoolbar()
        openhomepage()
        name = sharedPreferences_profie.getString("name", "Undefined")
        email = sharedPreferences_profie.getString("email", "hello")
        txtHeaderName.text = name
        txtHeaderEmail.text = email

        val actionBarDrawerToggle = ActionBarDrawerToggle(  // also used to make hamburger icon
            this@MainActivity, DrawerLayout, R.string.open_drawer, R.string.close_drawer
        )

        DrawerLayout.addDrawerListener(actionBarDrawerToggle) // used to make Hamburger Icon
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previous != null)
            {
                previous?.isChecked = false
            }

            it.isChecked = true
            it.isCheckable = true
            previous = it

            when(it.itemId)
            {
                R.id.HomeItem -> {
                    openhomepage()
                    DrawerLayout.closeDrawers()
                    Toast.makeText(this@MainActivity, "Clicked on Home", Toast.LENGTH_SHORT).show()
                }

                R.id.FavouriteItem ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, FavouriteFragment())
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    DrawerLayout.closeDrawers()
                    Toast.makeText(this@MainActivity, "Clicked on Favourites", Toast.LENGTH_SHORT).show()
                }

                R.id.ProfileItem ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "Profile"
                    DrawerLayout.closeDrawers()
                    Toast.makeText(this@MainActivity, "Clicked on Profile", Toast.LENGTH_SHORT).show()

                }

                R.id.HistoryItem ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, HistoryFragment())
                        .commit()
                    supportActionBar?.title = "Order History"
                    DrawerLayout.closeDrawers()
                    Toast.makeText(this@MainActivity, "Clicked on History", Toast.LENGTH_SHORT).show()

                }

                R.id.LogOutItem ->
                {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure, You want to log out")
                    dialog.setPositiveButton("yes")
                    {
                            text, listener ->
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        sharedpreferences.edit().clear().apply()
                        this.finish()

                    }
                    dialog.setNegativeButton("no")
                    {
                            text, listener ->
                       dialog.setCancelable(true)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }




    } // oncreate close

    fun setuptoolbar()
    {
        setSupportActionBar(ToolBarLayout)
        supportActionBar?.title = "Welcome to Food Mania"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            DrawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openhomepage()
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.FrameLayout, HomeFragment())
            .commit()
            supportActionBar?.title = "All Restaurants"
            navigationView.setCheckedItem(R.id.HomeItem)
    }

    override fun onBackPressed() {

        if (this.DrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.DrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {


            val flag = supportFragmentManager.findFragmentById(R.id.FrameLayout)
            when (flag) {
                !is HomeFragment -> openhomepage()

                else -> super.onBackPressed()
            }
        }
    }


}