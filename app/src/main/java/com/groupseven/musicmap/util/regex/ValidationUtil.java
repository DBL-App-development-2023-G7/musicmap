package com.groupseven.musicmap.util.regex;

import android.util.Patterns;

import com.groupseven.musicmap.util.Constants;

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
        if (!Constants.USERNAME_PATTERN.matcher(username).matches()) {
            return Result.FORMAT;
        }
        return Result.VALID;
    }

    public static Result isIdentifierValid(String identifier) {
        if (identifier.isEmpty()) {
            return Result.EMPTY;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(identifier).matches()
                && !Constants.USERNAME_PATTERN.matcher(identifier).matches()) {
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
        if (!Constants.PASSWORD_PATTERN.matcher(password).matches()) {
            return Result.FORMAT;
        }
        return Result.VALID;
    }

}
