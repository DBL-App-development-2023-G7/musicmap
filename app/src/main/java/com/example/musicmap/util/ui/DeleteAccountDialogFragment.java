package com.example.musicmap.util.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.musicmap.R;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.regex.ValidationUtil;

public class DeleteAccountDialogFragment extends DialogFragment {

    private EditText passwordInput;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.delete_accout_dialog, null);
        passwordInput = dialogView.findViewById(R.id.password_dialog_editText);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog alertDialog = builder.setView(dialogView)
                // Add title to the dialog
                .setTitle(R.string.delete_account_dialog_title)
                // Add description message to the dialog
                .setMessage(R.string.delete_account_dialog_message)
                // Add action buttons
                .setPositiveButton(R.string.delete_account, null).setNegativeButton(R.string.cancel,
                        (dialog, id) -> dialog.dismiss()).create();

        // Required so that the dialog won't close when an error occurs after pressing delete account
        alertDialog.setOnShowListener(dialog -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> deleteAccount());
        });

        return alertDialog;
    }

    private void deleteAccount() {
        String password = passwordInput.getText().toString();

        if (checkPassword(password)) {
            AuthSystem.deleteUser(password).addOnFailureListener(exception -> {
                String message = exception.getMessage();
                passwordInput.setError(message);
            });
        }
    }

    private boolean checkPassword(String password) {
        switch (ValidationUtil.isPasswordValid(password)) {
            case EMPTY:
                passwordInput.setError(getString(R.string.input_error_enter_password));
                return false;
            case FORMAT:
                passwordInput.setError(getString(R.string.input_error_valid_password));
                return false;
            case VALID:
                return true;
            default:
                passwordInput.setError(getString(R.string.input_error_unexpected));
                return false;
        }
    }

}
