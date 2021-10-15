package com.khedr.runtracking.presentation.ui.user_setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentBodySetupBeginBinding


class BodySetupBeginFragment : Fragment() {
    private lateinit var b:FragmentBodySetupBeginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b=DataBindingUtil.inflate(inflater,R.layout.fragment_body_setup_begin, container, false)
        return b.root
    }


}