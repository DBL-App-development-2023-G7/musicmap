package com.groupseven.musicmap.util.regex;

import android.content.Context;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.firebase.Queries;

import java.util.concurrent.CompletableFuture;

public class InputChecker {

    public static boolean checkEmail(String email, EditText emailInput) {
        Context context = emailInput.getContext();
        switch (ValidationUtil.isEmailValid(email)) {
            case EMPTY:
                emailInput.setError(context.getString(R.string.input_error_enter_email));
                return false;
            case FORMAT:
                emailInput.setError(context.getString(R.string.input_error_valid_email));
                return false;
            case VALID:
                return true;
            default:
                emailInput.setError(context.getString(R.string.input_error_unexpected));
                return false;
        }
    }

    public static boolean checkIdentifier(String identifier, EditText identifierInput) {
        Context context = identifierInput.getContext();
        switch (ValidationUtil.isIdentifierValid(identifier)) {
            case EMPTY:
                identifierInput.setError(context.getString(R.string.input_error_enter_email));
                return false;
            case FORMAT:
                identifierInput.setError(context.getString(R.string.input_error_valid_email));
                return false;
            case VALID:
                return true;
            default:
                identifierInput.setError(context.getString(R.string.input_error_unexpected));
                return false;
        }
    }

    public static boolean checkPassword(String password, EditText passwordInput) {
        Context context = passwordInput.getContext();
        switch (ValidationUtil.isPasswordValid(password)) {
            case EMPTY:
                passwordInput.setError(context.getString(R.string.input_error_enter_password));
                return false;
            case FORMAT:
                passwordInput.setError(context.getString(R.string.input_error_valid_password));
                return false;
            case VALID:
                return true;
            default:
                passwordInput.setError(context.getString(R.string.input_error_unexpected));
                return false;
        }
    }

    public static boolean checkFirstName(String firstName, EditText firstNameInput) {
        Context context = firstNameInput.getContext();
        switch (ValidationUtil.isMandatoryFieldValid(firstName)) {
            case EMPTY:
                firstNameInput.setError(context.getString(R.string.input_error_enter_first_name));
                return false;
            case VALID:
                return true;
            default:
                firstNameInput.setError(context.getString(R.string.input_error_unexpected));
                return false;
        }
    }

    public static boolean checkLastName(String lastName, EditText lastNameInput) {
        Context context = lastNameInput.getContext();
        switch (ValidationUtil.isMandatoryFieldValid(lastName)) {
            case EMPTY:
                lastNameInput.setError(context.getString(R.string.input_error_enter_last_name));
                return false;
            case VALID:
                return true;
            default:
                lastNameInput.setError(context.getString(R.string.input_error_unexpected));
                return false;
        }
    }

    public static CompletableFuture<Boolean> checkUsername(String username, EditText usernameInput) {
        Context context = usernameInput.getContext();
        switch (ValidationUtil.isUsernameValid(username)) {
            case EMPTY:
                usernameInput.setError(context.getString(R.string.input_error_enter_username));
                return CompletableFuture.completedFuture(false);
            case FORMAT:
                usernameInput.setError(context.getString(R.string.input_error_valid_username));
                return CompletableFuture.completedFuture(false);
            case VALID:
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                Queries.getUserWithUsername(username).whenCompleteAsync((user, throwable) -> {
                    if (throwable == null) {
                        if (user != null) {
                            usernameInput.setError(context.getString(R.string.input_error_username_exists));
                            future.complete(false);
                        } else {
                            future.complete(true);
                        }
                    } else {
                        usernameInput.setError("Cannot check username");
                        future.complete(false);
                    }
                }, ContextCompat.getMainExecutor(usernameInput.getContext()));
                return future;
            default:
                usernameInput.setError(context.getString(R.string.input_error_unexpected));
                return CompletableFuture.completedFuture(false);
        }
    }

    public static boolean checkRepeatPassword(String repeatPassword, String password, EditText repeatPasswordInput) {
        Context context = repeatPasswordInput.getContext();
        if (!repeatPassword.equals(password)) {
            repeatPasswordInput.setError(context.getString(R.string.input_error_passwords_not_matching));
            return false;
        }
        repeatPasswordInput.setError(null);
        return true;
    }

}
