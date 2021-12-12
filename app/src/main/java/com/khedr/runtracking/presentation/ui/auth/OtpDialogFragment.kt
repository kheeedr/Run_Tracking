package com.khedr.runtracking.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.DialogEnterOtpBinding
import com.khedr.runtracking.utils.disableUserDragging
import com.khedr.runtracking.utils.hideKeyboard
import com.khedr.runtracking.utils.makeDialogFullExpanded
import timber.log.Timber

class OtpDialogFragment : BottomSheetDialogFragment() {

    private lateinit var b: DialogEnterOtpBinding
    private lateinit var auth: FirebaseAuth
    private val safeArgs: OtpDialogFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        b.btBackOtp.setOnClickListener {
            findNavController().navigateUp()
        }

        b.etVerifyCodeOtp.doAfterTextChanged { code ->
            if (code?.length == 6) {
                b.etVerifyCodeOtp.isEnabled = false
                b.layoutResend.visibility = View.INVISIBLE
                b.progressEnterVerifyCode.visibility = View.VISIBLE
                val credential =
                    PhoneAuthProvider.getCredential(safeArgs.verificationId, code.toString())
                signInWithPhoneAuthCredential(credential)
            }

        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //val user = task.result?.user
                    navigateToSetupBody()
                } else {

                    Timber.d("signInWithCredential:failure" + task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            requireContext(),
                            "The verification code entered was invalid",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    // Update UI
                    b.etVerifyCodeOtp.isEnabled = true
                    b.layoutResend.visibility = View.VISIBLE
                    b.progressEnterVerifyCode.visibility = View.INVISIBLE

                }
            }
    }

    private fun navigateToSetupBody() {
        hideKeyboard()
        val action =
            OtpDialogFragmentDirections.actionEnterOtpDialogFragmentToBodySetupNavigation()
        findNavController().navigate(action)
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