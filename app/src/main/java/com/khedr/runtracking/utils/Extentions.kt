package com.khedr.runtracking.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import android.R.color

import android.graphics.BlendModeColorFilter

import android.os.Build
import com.khedr.runtracking.R


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

//fun View.disable() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
//            Color.GRAY,
//            BlendModeCompat.MULTIPLY
//        )
//    } else {
//        background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
//    }
//    isClickable = false
//}
//
//fun View.enable() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
//            context.getColor(R.color.primary_color), BlendModeCompat.MULTIPLY
//        )
//    } else {
//        background.setColorFilter(
//            context.getColor(R.color.primary_color), PorterDuff.Mode.SRC_ATOP
//        )
//    }
//    isClickable = true
//}