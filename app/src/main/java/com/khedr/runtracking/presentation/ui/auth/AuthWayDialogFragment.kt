package com.khedr.runtracking.presentation.ui.auth

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.DialogChooseAuthWayBinding


class AuthWayDialogFragment : BottomSheetDialogFragment() {

    private lateinit var b: DialogChooseAuthWayBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.dialog_choose_auth_way, container, false)
        return b.root

    }
}