package com.example.musicmap.util.regex;

import android.util.Patterns;

public class ValidationUtil {

    public enum Result {
        VALID,
        FORMAT,
        EMPTY
    }

    public static Result isMandatoryFieldValid(String string) {
        if (string.isEmpty()) {
            return Result.EMPTY;
        }
        return Result.VALID;
    }

    public static Result isUsernameValid(String username) {
        if (username.isEmpty()) {
            return Result.EMPTY;
        }
        if (!MMPatterns.USERNAME.matcher(username).matches()) {
            return Result.FORMAT;
        }
        return Result.VALID;
    }

    public static Result isEmailValid(String email) {
        if (email.isEmpty()) {
            return Result.EMPTY;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.FORMAT;
        }
        return Result.VALID;
    }

    public static Result isPasswordValid(String password) {
        if (password.isEmpty()) {
            return Result.EMPTY;
        }
        //TODO implement an invalid format for passwords & discuss about it
        return Result.VALID;
    }

}
