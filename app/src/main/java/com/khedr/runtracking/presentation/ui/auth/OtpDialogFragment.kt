package com.khedr.runtracking.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.DialogEnterOtpBinding
import com.khedr.runtracking.utils.disableUserDragging
import com.khedr.runtracking.utils.hideKeyboard
import com.khedr.runtracking.utils.makeDialogFullExpanded


class OtpDialogFragment : BottomSheetDialogFragment() {

    private lateinit var b: DialogEnterOtpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.btBackOtp.setOnClickListener {
            findNavController().navigateUp()
        }
        b.tvHeaderWelcome.setOnClickListener {
            hideKeyboard()
            val action =
                OtpDialogFragmentDirections.actionEnterOtpDialogFragmentToBodySetupNavigation()
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = DataBindingUtil.inflate(inflater, R.layout.dialog_enter_otp, container, false)

        dialog?.setCanceledOnTouchOutside(false)
        makeDialogFullExpanded()
        disableUserDragging()

        return b.root

    }
}