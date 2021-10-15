package com.khedr.runtracking.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var b: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)

        return b.root

    }


}