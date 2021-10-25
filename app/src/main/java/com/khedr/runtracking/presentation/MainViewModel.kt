package com.khedr.runtracking.presentation

import androidx.lifecycle.ViewModel
import com.khedr.runtracking.data.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject
constructor(repository: RunRepository) : ViewModel() {

}