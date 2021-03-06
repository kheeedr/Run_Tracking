package com.khedr.runtracking.presentation.ui.main_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentProfileBinding
import com.khedr.runtracking.databinding.FragmentProgramBinding


class ProgramFragment : Fragment() {
    private lateinit var b:FragmentProgramBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.fragment_program, container, false)
        return b.root
    }


}