package com.khedr.runtracking.presentation.ui.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.DialogEnterPhoneBinding
import com.khedr.runtracking.utils.Constants
import com.khedr.runtracking.utils.hideKeyboard
import com.khedr.runtracking.utils.makeDialogFullExpanded
import java.lang.reflect.Field
import androidx.annotation.NonNull
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.khedr.runtracking.utils.disableUserDragging


class EnterPhoneDialogFragment : BottomSheetDialogFragment() {

    private lateinit var b: DialogEnterPhoneBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        b.btToOtp.setOnClickListener {
            hideKeyboard()
            val action =
                EnterPhoneDialogFragmentDirections.actionEnterPhoneDialogFragmentToEnterOtpDialogFragment()
            findNavController().navigate(action)

        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = DataBindingUtil.inflate(inflater, R.layout.dialog_enter_phone, container, false)
        dialog?.setCanceledOnTouchOutside(false)
        makeDialogFullExpanded()
        disableUserDragging()
        return b.root
    }


}