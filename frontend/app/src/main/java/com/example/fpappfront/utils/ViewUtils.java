package com.example.fpappfront.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputLayout;

public class ViewUtils {

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void clearErrors(TextInputLayout... inputs) {
        for (TextInputLayout input : inputs) {
            input.setError(null);
        }
    }
}