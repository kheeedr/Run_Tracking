package com.khedr.runtracking.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentWelcomeBinding
import com.khedr.runtracking.presentation.ui.adapter.WelcomeAdapter

class WelcomeFragment : Fragment(), View.OnClickListener {
    private lateinit var b: FragmentWelcomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btGetStarted.setOnClickListener(this)
        b.rvWelcome.adapter = WelcomeAdapter()
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(b.rvWelcome)
        b.indicator.attachToRecyclerView(b.rvWelcome, pagerSnapHelper)
        refreshViewBasedOnRecyclerView()



    }

    override fun onClick(v: View?) {
        if (v == b.btGetStarted) {
            val position =
                (b.rvWelcome.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (position < 2) {
                b.rvWelcome.post { b.rvWelcome.smoothScrollToPosition(position + 1) }
            } else {
                val action =
                    WelcomeFragmentDirections.actionWelcomeFragmentToAuthNavigation()
                findNavController().navigate(action)
            }
        }
    }


    private fun refreshViewBasedOnRecyclerView() {

        b.rvWelcome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position =
                        (b.rvWelcome.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (position < 2) {
                        b.btGetStarted.text = "Next"

                    } else {
                        b.btGetStarted.text = "Get Started"
                    }
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        return b.root
    }
}