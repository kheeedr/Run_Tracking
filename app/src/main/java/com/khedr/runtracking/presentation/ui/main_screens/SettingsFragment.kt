package com.khedr.runtracking.presentation.ui.main_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private lateinit var b: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return b.root
    }

}