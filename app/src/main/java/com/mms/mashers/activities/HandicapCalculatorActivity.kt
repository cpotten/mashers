package com.mms.mashers.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.mms.mashers.R
import kotlin.math.roundToInt

class HandicapCalculatorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handicap_calculator)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        val actionBarTitle: TextView = findViewById(R.id.action_bar_title)
        actionBarTitle.text = "HANDICAP"

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val burgerMenuIcon = findViewById<ImageView>(R.id.burger_menu_icon)
        burgerMenuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView)
            } else {
                drawerLayout.openDrawer(navigationView)
            }
        }

        val handicapIndexInput = findViewById<EditText>(R.id.handicapIndexInput)
        val slopeRatingInput = findViewById<EditText>(R.id.slopeRatingInput)
        val calculateHandicapButton = findViewById<Button>(R.id.calculateHandicapButton)
        val courseHandicapTextView = findViewById<TextView>(R.id.courseHandicapTextView)

        calculateHandicapButton.setOnClickListener {
            val handicapIndex = handicapIndexInput.text.toString().toFloatOrNull()
            val slopeRating = slopeRatingInput.text.toString().toFloatOrNull()

            if (handicapIndex != null && slopeRating != null) {
                val courseHandicap = calculateCourseHandicap(handicapIndex, slopeRating)
                val roundedCourseHandicap = courseHandicap.roundToInt()
                courseHandicapTextView.text = "Handicap: $roundedCourseHandicap"
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_random_generator -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_handicap_calculator -> {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
            R.id.nav_handicap_tracker -> {
                val intent = Intent(this, HandicapTrackerActivity::class.java)
                startActivity(intent)
            }
            // Add more cases for other menu items
        }

        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    private fun calculateCourseHandicap(handicapIndex: Float, slopeRating: Float): Float {
        val courseHandicap = handicapIndex * (slopeRating / 113)
        return courseHandicap
    }
}
