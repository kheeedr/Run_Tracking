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
import com.khedr.runtracking.databinding.FragmentGetTallBinding
import java.util.function.Consumer


class GetTallFragment : Fragment() {
    private lateinit var b: FragmentGetTallBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btToGetTall.setOnClickListener {
            val action = GetTallFragmentDirections.actionGetTallFragmentToGetWeightFragment()
            findNavController().navigate(action)

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_get_tall, container, false)

        val listOfItems = (100..230).toMutableList().map { i -> i.toString() }

        b.wheelViewGetTall.apply {
            setInitPosition(70)
            setCanLoop(false)
            setTextSize(resources.getDimensionPixelSize(R.dimen._12sdp).toFloat())
            setItems(listOfItems)
        }


        return b.root
    }


}