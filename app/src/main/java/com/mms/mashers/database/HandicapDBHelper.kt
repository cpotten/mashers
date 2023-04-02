package com.mms.mashers.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HandicapDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HandicapTracker.db"
        const val TABLE_NAME = "handicap_data"
        const val COLUMN_ID = "_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_COURSE_NAME = "course_name"
        const val COLUMN_HANDICAP = "handicap"
        const val COLUMN_TEE_COLOR = "tee_color"
        const val COLUMN_GROSS_SCORE = "gross_score"
        const val COLUMN_COURSE_RATING = "course_rating"
        const val COLUMN_SLOPE_RATING = "slope_rating"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_COURSE_NAME TEXT" +
                "$COLUMN_HANDICAP REAL," +
                "$COLUMN_TEE_COLOR TEXT," +
                "$COLUMN_GROSS_SCORE INTEGER," +
                "$COLUMN_COURSE_RATING REAL," +
                "$COLUMN_SLOPE_RATING REAL" +
                ")"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertHandicapData(date: String, courseName: String, handicap: Float, teeColor: String, grossScore: Int, courseRating: Float, slopeRating: Float) {
        val values = ContentValues()
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_COURSE_NAME, courseName)
        values.put(COLUMN_HANDICAP, handicap)
        values.put(COLUMN_TEE_COLOR, teeColor)
        values.put(COLUMN_GROSS_SCORE, grossScore)
        values.put(COLUMN_COURSE_RATING, courseRating)
        values.put(COLUMN_SLOPE_RATING, slopeRating)

        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Add additional methods for querying, updating, and deleting data as needed
}
