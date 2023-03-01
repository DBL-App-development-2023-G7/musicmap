package com.example.musicmap.util.regex;

import android.util.Patterns;

public class ValidationUtil {
    public enum ValidationResult {
        VALID,
        FORMAT,
        EMPTY
    }

    public static ValidationResult isMandatoryFieldValid(String string) {
        if (string.equals("")) {
            return ValidationResult.EMPTY;
        }
        return ValidationResult.VALID;
    }

    public static ValidationResult isUsernameValid(String username) {
        if (username.equals("")) {
            return ValidationResult.EMPTY;
        }
        if (!MMPatterns.USERNAME.matcher(username).matches()) {
            return ValidationResult.FORMAT;
        }
        return ValidationResult.VALID;
    }

    public static ValidationResult isEmailValid(String email) {
        if (email.equals("")) {
            return ValidationResult.EMPTY;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult.FORMAT;
        }
        return ValidationResult.VALID;
    }

    public static ValidationResult isPasswordValid(String password) {
        if (password.equals("")) {
            return ValidationResult.EMPTY;
        }
        //TODO implement a invalid format for passwords & discuss about it
        return ValidationResult.VALID;
    }
}