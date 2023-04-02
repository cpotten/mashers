package com.mms.mashers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mms.mashers.database.HandicapDBHelper
import com.mms.mashers.database.HandicapEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HandicapHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val handicapDBHelper: HandicapDBHelper = HandicapDBHelper(application)
    val allHandicapEntries: LiveData<List<HandicapEntry>>

    init {
        allHandicapEntries = handicapDBHelper.getAllHandicapEntries()
    }

    fun deleteHandicapEntry(handicapEntry: HandicapEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            handicapDBHelper.deleteHandicapEntry(handicapEntry)
        }
    }
}
