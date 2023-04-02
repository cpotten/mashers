package com.mms.mashers.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlin.math.min
import android.widget.Toast
import com.mms.mashers.database.HandicapDBHelper
import com.mms.mashers.database.HandicapEntry
import com.mms.mashers.R
import java.text.SimpleDateFormat
import java.util.*

class HandicapTrackerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var dbHelper: HandicapDBHelper
    private lateinit var datePlayedInput: EditText
    private lateinit var courseNameInput: EditText
    private lateinit var playingHandicapInput: EditText
    private lateinit var teeColourInput: EditText
    private lateinit var grossScoreInput: EditText
    private lateinit var courseRatingInput: EditText
    private lateinit var slopeRatingInput: EditText
    private lateinit var currentHandicapDisplay: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handicap_tracker)

        dbHelper = HandicapDBHelper(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        val actionBarTitle: TextView = findViewById(R.id.action_bar_title)
        actionBarTitle.text = "TRACKER"

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

        datePlayedInput = findViewById(R.id.date_played_input)
        courseNameInput = findViewById(R.id.course_name_input)
        playingHandicapInput = findViewById(R.id.playing_handicap_input)
        teeColourInput = findViewById(R.id.tee_colour_input)
        grossScoreInput = findViewById(R.id.gross_score_input)
        courseRatingInput = findViewById(R.id.course_rating_input)
        slopeRatingInput = findViewById(R.id.slope_rating_input)
        currentHandicapDisplay = findViewById(R.id.current_handicap_display)

        val saveButton = findViewById<Button>(R.id.save_button)
        val viewHistoryButton = findViewById<Button>(R.id.view_history_button)

        datePlayedInput.setOnClickListener {
            showDatePickerDialog()
        }

        saveButton.setOnClickListener {
            saveHandicapEntry()
        }

        viewHistoryButton.setOnClickListener {
            val intent = Intent(this, HandicapHistoryActivity::class.java)
            startActivity(intent)
        }

        updateCurrentHandicapDisplay()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }.time
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
                datePlayedInput.setText(dateFormatter.format(date))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveHandicapEntry() {
        val datePlayed = datePlayedInput.text.toString()
        val courseName = courseNameInput.text.toString()
        val playingHandicap = playingHandicapInput.text.toString().toFloatOrNull()
        val teeColour = teeColourInput.text.toString()
        val grossScore = grossScoreInput.text.toString().toIntOrNull()
        val courseRating = courseRatingInput.text.toString().toFloatOrNull()
        val slopeRating = slopeRatingInput.text.toString().toFloatOrNull()

        if (playingHandicap != null && grossScore != null && courseRating != null && slopeRating != null) {
            val entry = HandicapEntry(
                datePlayed,
                courseName,
                playingHandicap,
                teeColour,
                grossScore,
                courseRating,
                slopeRating
            )
            dbHelper.addHandicapEntry(entry)
            updateCurrentHandicapDisplay()
        } else {
            Toast.makeText(this, "Please fill in all fields with valid values.", Toast.LENGTH_LONG).show()
            // Show an error message if any of the input values are invalid
        }
    }


    private fun updateCurrentHandicapDisplay() {
        val entries = dbHelper.getAllHandicapEntries()
        if (entries.isNotEmpty()) {
            val currentHandicap = calculateHandicap(entries)
            currentHandicapDisplay.text = String.format("%.1f", currentHandicap)
        } else {
            currentHandicapDisplay.text = "-"
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
                val intent = Intent(this, HandicapCalculatorActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_handicap_tracker -> {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
            // Add more cases for other menu items
        }

        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    private fun calculateHandicap(entries: List<HandicapEntry>): Float {


        // 1. Calculate the Handicap Differential for each round
        val handicapDifferentials = entries.map {
            (it.grossScore - it.courseRating) * (113 / it.slopeRating)
        }.sorted()

        // 2. Select the lowest Handicap Differentials (best scores) based on the number of rounds played
        val roundsPlayed = entries.size
        val numBestScores = when {
            roundsPlayed <= 5 -> 1
            roundsPlayed <= 10 -> min(3, roundsPlayed - 3)
            roundsPlayed <= 20 -> min(8, roundsPlayed - 10)
            else -> 10
        }
        val bestDifferentials = handicapDifferentials.take(numBestScores)

        // 3. Calculate the average of the selected Handicap Differentials
        val average = bestDifferentials.sum() / bestDifferentials.size

        // 4. Multiply the average by 0.93 to get the final Handicap Index
        return (average * 0.93).toFloat()
    }
}
