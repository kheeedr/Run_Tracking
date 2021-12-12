package com.khedr.runtracking.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.DialogEnterPhoneBinding
import com.khedr.runtracking.utils.*
import timber.log.Timber
import java.util.concurrent.TimeUnit


class EnterPhoneDialogFragment : BottomSheetDialogFragment() {

    private lateinit var b: DialogEnterPhoneBinding


    private lateinit var auth: FirebaseAuth

    private lateinit var phoneNumber: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        listenToEditTextChanges()
        b.btToOtp.setOnClickListener {
            if (isValidPhone()) {
                sendVerifyCode()
                b.progressEnterPhone.visibility = View.VISIBLE
                b.btToOtp.visibility = View.GONE
            }

        }
    }

    private fun isValidPhone(): Boolean {

        val phone = b.etPhone.text.toString()

        if (phone.isEmpty()) {
            b.layoutEtPhone.error = "Phone number can't be empty"
            b.layoutEtPhone.requestFocus()
            return false
        } else if (phone.length < 10) {
            b.layoutEtPhone.error = "Phone number is too short"
            b.layoutEtPhone.requestFocus()
            return false
        }
        b.layoutEtPhone.isErrorEnabled = false
        return true
    }

    private fun listenToEditTextChanges() =
        b.etPhone.doOnTextChanged { text, _, _, count ->
            if (count > 0 && text!![0] == '0') {
                b.etPhone.text!!.clear()
            }
        }


    fun navigateToOtpVerifying(verificationId: String) {
        hideKeyboard()
        val action =
            EnterPhoneDialogFragmentDirections.actionEnterPhoneDialogFragmentToEnterOtpDialogFragment(
                phoneNumber,
                verificationId
            )
        findNavController().navigate(action)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Timber.d("onVerificationFailed$e")

            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(
                    requireContext(),
                    "The format of the phone number provided is incorrect",
                    Toast.LENGTH_LONG
                ).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(requireContext(), "Sorry Try again later", Toast.LENGTH_SHORT).show()
            }

            // Show a message and update the UI

            b.progressEnterPhone.visibility = View.GONE
            b.btToOtp.visibility = View.VISIBLE
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            navigateToOtpVerifying(verificationId)

            b.progressEnterPhone.visibility = View.GONE
            b.btToOtp.visibility = View.VISIBLE
        }
    }

    private fun sendVerifyCode() {
        phoneNumber =
            when (b.etPhone.text!![0]) {
                '+' -> b.etPhone.text!!.toString()
                '0' -> '+' + b.ccp.selectedCountryCode + b.etPhone.text!!.toString().drop(1)
                else -> '+' + b.ccp.selectedCountryCode + b.etPhone.text!!.toString()
            }

        Timber.d(phoneNumber)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)                // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)       // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)                        // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
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