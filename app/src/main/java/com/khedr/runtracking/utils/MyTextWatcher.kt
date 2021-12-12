/*
 * Copyright (c) 2021.
 * Created by Mohamed Khedr.
 */
package com.khedr.runtracking.utils

import android.text.Editable
import android.text.TextWatcher

abstract class MyTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {}
}