package com.mms.mashers.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mms.mashers.database.HandicapDBHelper
import com.mms.mashers.database.HandicapEntry

class HandicapTrackerViewModel : ViewModel() {

    private val dbHelper = HandicapDBHelper()

    val courseName = MutableLiveData<String>()
    val datePlayed = MutableLiveData<String>()
    val playingHandicap = MutableLiveData<String>()
    val teeColour = MutableLiveData<String>()
    val grossScore = MutableLiveData<String>()
    val courseRating = MutableLiveData<String>()
    val slopeRating = MutableLiveData<String>()

    fun saveHandicapData() {
        val handicapEntry = HandicapEntry(
            courseName = courseName.value ?: "",
            datePlayed = datePlayed.value ?: "",
            playingHandicap = playingHandicap.value?.toDoubleOrNull() ?: 0.0,
            teeColour = teeColour.value ?: "",
            grossScore = grossScore.value?.toDoubleOrNull() ?: 0.0,
            courseRating = courseRating.value?.toDoubleOrNull() ?: 0.0,
            slopeRating = slopeRating.value?.toDoubleOrNull() ?: 0.0
        )

        dbHelper.addHandicapEntry(handicapEntry)
    }
}
