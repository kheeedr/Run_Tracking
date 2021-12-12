package com.khedr.runtracking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khedr.runtracking.data.repository.RunRepository
import com.khedr.runtracking.domain.model.Run
import com.khedr.runtracking.domain.model.toRunEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject
constructor(private val repository: RunRepository) : ViewModel() {

    fun saveRunToDatabase(run: Run) = viewModelScope.launch {
        repository.insertRun(run.toRunEntity())
    }

}