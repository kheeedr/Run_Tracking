package com.khedr.runtracking.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentChooseAuthWayBinding


class AuthWayFragment : Fragment(){

    private lateinit var b: FragmentChooseAuthWayBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        b.btPhoneChooseAuth.setOnClickListener {
            val action= AuthWayFragmentDirections.actionChooseAuthWayDialogFragmentToEnterPhoneDialogFragment()
            findNavController().navigate(action)
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_auth_way, container, false)
        return b.root
    }


}