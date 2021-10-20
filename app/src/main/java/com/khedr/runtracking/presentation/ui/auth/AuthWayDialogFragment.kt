package com.khedr.runtracking.presentation.ui.auth

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.DialogChooseAuthWayBinding
import com.khedr.runtracking.utils.Constants.AUTH_WAY
import com.khedr.runtracking.utils.Constants.GET_AUTH_WAY
import com.khedr.runtracking.utils.Constants.PHONE_AUTH
import com.khedr.runtracking.utils.makeDialogFullExpanded


class AuthWayDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var b: DialogChooseAuthWayBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        b.btPhoneChooseAuth.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v) {
            b.btPhoneChooseAuth -> {
                setFragmentResult(GET_AUTH_WAY, bundleOf(AUTH_WAY to PHONE_AUTH))
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = DataBindingUtil.inflate(inflater, R.layout.dialog_choose_auth_way, container, false)
        makeDialogFullExpanded()
        return b.root
    }


}