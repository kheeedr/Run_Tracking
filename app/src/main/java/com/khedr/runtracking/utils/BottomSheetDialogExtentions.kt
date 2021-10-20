package com.khedr.runtracking.utils

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.inputmethod.InputMethodManager


fun BottomSheetDialogFragment.makeDialogFullExpanded() {

    dialog?.setOnShowListener {
        val bottomSheetDialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        val bottomSheet: FrameLayout? =
            bottomSheetDialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        if (bottomSheet != null)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
    }
}

fun BottomSheetDialogFragment.hideKeyboard() {
    val imm =
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)

}

fun BottomSheetDialogFragment.disableUserDragging() {
    if (dialog is BottomSheetDialog) {
        val behaviour = (dialog as BottomSheetDialog).behavior
        behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }
}

