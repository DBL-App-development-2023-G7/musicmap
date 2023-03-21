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

    public static Result isIdentifierValid(String identifier) {
        if (identifier.isEmpty()) {
            return Result.EMPTY;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(identifier).matches()
                && !MMPatterns.USERNAME.matcher(identifier).matches()) {
            return Result.FORMAT;
        }
        return  Result.VALID;
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
        //TODO discuss about the FORMAT error for passwords
        if (!MMPatterns.PASSWORD.matcher(password).matches()) {
            return Result.FORMAT;
        }
        return Result.VALID;
    }

}
