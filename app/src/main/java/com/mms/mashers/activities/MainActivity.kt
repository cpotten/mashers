package com.mms.mashers.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.content.Context
import android.widget.CheckedTextView
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mms.mashers.R

class MainActivity : AppCompatActivity() {
    private lateinit var teamsTextView: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var burgerMenuIcon: ImageView
    private lateinit var selectNamesButton: Button
    private lateinit var namesEditText: EditText
    private val storedNames = mutableListOf<String>()
    private lateinit var namesDbHelper: NamesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        namesDbHelper = NamesDatabaseHelper(this)

        namesEditText = findViewById(R.id.namesEditText)
        val generateTeamsButton = findViewById<Button>(R.id.generateTeamsButton)
        teamsTextView = findViewById(R.id.teamsTextView)

        generateTeamsButton.setOnClickListener {
            val inputNames = namesEditText.text.toString()
            val names = inputNames.split("\\s*,\\s*|\\s+\\n\\s*".toRegex())

            // Store the names in the SQLite database
            names.forEach { name ->
                namesDbHelper.addName(name)
            }

            val teams = generateRandomTeams(names, "Mix")
            displayTeams(teams)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        burgerMenuIcon = findViewById(R.id.burger_menu_icon)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_random_generator -> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_handicap_calculator -> {
                    startActivity(Intent(this, HandicapCalculatorActivity::class.java).apply {
                        putExtra("burgerMenuIconClickListener", burgerMenuIcon.hasOnClickListeners())
                    })
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_handicap_tracker -> {
                    startActivity(Intent(this, HandicapTrackerActivity::class.java).apply {
                        putExtra("burgerMenuIconClickListener", burgerMenuIcon.hasOnClickListeners())
                    })
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        burgerMenuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView)
            } else {
                drawerLayout.openDrawer(navigationView)
            }
        }

        selectNamesButton = findViewById(R.id.selectNamesButton)
        selectNamesButton.setOnClickListener { showMultiSelectDialog() }

        // Load stored names from the SQLite database
        storedNames.addAll(namesDbHelper.getAllNames())
    }

    private fun showMultiSelectDialog() {
        val multiSelectAdapter = MultiSelectAdapter(this, storedNames)
        val listView = ListView(this).apply {
            adapter = multiSelectAdapter
            dividerHeight = 0
        }

        AlertDialog.Builder(this)
            .setTitle("Select Names")
            .setView(listView)
            .setPositiveButton("Select") { _, _ ->
                val selectedNames = multiSelectAdapter.getSelectedItems()
                namesEditText.setText(selectedNames.joinToString(", "))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun generateRandomTeams(namesList: List<String>, teamType: String): List<List<String>> {
        val shuffledNames = namesList.shuffled()
        val teams = mutableListOf<List<String>>()
        var remainingNames = shuffledNames

        if (teamType == "Mix") {
            val totalNames = remainingNames.size
            var numberOfFourPlayerTeams = totalNames / 4
            var numberOfThreePlayerTeams = (totalNames - numberOfFourPlayerTeams * 4) / 3

            while (numberOfFourPlayerTeams * 4 + numberOfThreePlayerTeams * 3 != totalNames) {
                numberOfFourPlayerTeams -= 1
                numberOfThreePlayerTeams = (totalNames - numberOfFourPlayerTeams * 4) / 3
            }

            // First add groups of 3
            for (i in 0 until numberOfThreePlayerTeams) {
                val currentTeam = remainingNames.take(3)
                teams.add(currentTeam)
                remainingNames = remainingNames.drop(3)
            }

            // Then add groups of 4
            for (i in 0 until numberOfFourPlayerTeams) {
                val currentTeam = remainingNames.take(4)
                teams.add(currentTeam)
                remainingNames = remainingNames.drop(4)
            }
        } else {
            val teamSize = teamType.toInt()
            while (remainingNames.isNotEmpty()) {
                val currentTeam = remainingNames.take(teamSize)
                teams.add(currentTeam)
                remainingNames = remainingNames.drop(teamSize)
            }
        }

        return teams
    }

    private fun displayTeams(teams: List<List<String>>) {
        val stringBuilder = StringBuilder()
        teams.forEachIndexed { index, team ->
            stringBuilder.append("Group ${index + 1}: ${team.joinToString(", ")}\n\n")
        }
        teamsTextView.text = stringBuilder.toString()
    }

    private inner class MultiSelectAdapter(context: Context, private val items: List<String>) :
        ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, items) {

        private val selectedItems = mutableSetOf<String>()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent) as CheckedTextView
            view.setOnClickListener {
                view.toggle()
                if (view.isChecked) {
                    selectedItems.add(items[position])
                } else {
                    selectedItems.remove(items[position])
                }
            }
            return view
        }

        fun getSelectedItems(): List<String> = selectedItems.toList()
    }

    class NamesDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "names.db"
            private const val DATABASE_VERSION = 1
            private const val TABLE_NAMES = "names"
            private const val KEY_ID = "id"
            private const val KEY_NAME = "name"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            val createTableQuery = "CREATE TABLE $TABLE_NAMES ($KEY_ID INTEGER PRIMARY KEY, $KEY_NAME TEXT)"
            db?.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAMES")
            onCreate(db)
        }

        fun addName(name: String) {
            if (!nameExists(name)) {
                val db = this.writableDatabase
                val contentValues = ContentValues()
                contentValues.put(KEY_NAME, name)
                db.insert(TABLE_NAMES, null, contentValues)
                db.close()
            }
        }

        private fun nameExists(name: String): Boolean {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NAMES WHERE $KEY_NAME = ?", arrayOf(name))
            val exists = cursor.moveToFirst()
            cursor.close()
            db.close()
            return exists
        }

        fun getAllNames(): MutableList<String> {
            val names = mutableListOf<String>()
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NAMES", null)

            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                    names.add(name)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()

            return names
        }
    }

    override fun onDestroy() {
        namesDbHelper.close()
        super.onDestroy()
    }
}
