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
import com.khedr.runtracking.databinding.FragmentGetBodyTypeBinding


class GetBodyTypeFragment : Fragment() {
    private lateinit var b: FragmentGetBodyTypeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btToGetWantedWeight.setOnClickListener {
            val action = GetBodyTypeFragmentDirections.actionGetBodyTypeFragmentToGetWantedWeightFragment()
            findNavController().navigate(action) }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b= DataBindingUtil.inflate(inflater, R.layout.fragment_get_body_type, container, false)
        return b.root
    }


}