package com.khedr.runtracking.presentation.ui.main_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var b: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btToSettings.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileItemToSettingsFragment()
            findNavController().navigate(action)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return b.root
    }


}