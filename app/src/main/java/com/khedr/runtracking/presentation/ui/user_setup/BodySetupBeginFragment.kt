package com.khedr.runtracking.presentation.ui.user_setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentBodySetupBeginBinding


class BodySetupBeginFragment : Fragment(), View.OnClickListener {
    private lateinit var b: FragmentBodySetupBeginBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btToGetName.setOnClickListener(this)
        b.btSkipSetup.setOnClickListener(this)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.fragment_body_setup_begin, container, false)
        return b.root
    }

    override fun onClick(v: View) {
        when (v) {
            b.btToGetName -> {
                val action =
                    BodySetupBeginFragmentDirections.actionBodySetupBeginFragmentToGetNameFragment()
                findNavController().navigate(action)
            }
            b.btSkipSetup -> {
                val action =
                    BodySetupBeginFragmentDirections.actionGlobalFromSetupBodyToHomeNavigation()
                findNavController().navigate(action)
            }
        }
    }


}