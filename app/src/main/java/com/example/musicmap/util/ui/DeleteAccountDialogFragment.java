package com.example.musicmap.util.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

        View view = inflater.inflate(R.layout.delete_accout_dialog, null);
        passwordInput = view.findViewById(R.id.password_dialog);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add title to the dialog
                .setTitle("Are you sure you want to delete your account?")
                // Add description message to the dialog
                .setMessage("If you delete your account all of your data will be lost and you won't be able to "
                        + "get it back. Please enter your password to confirm that you want to delete your account.")
                // Add action buttons
                .setPositiveButton("Delete Account", (dialog, id) -> deleteAccount())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    private void deleteAccount() {
        String password = String.valueOf(passwordInput.getText());

        if (checkPassword(password)) {
            AuthSystem.deleteUser(password).addOnFailureListener(task -> {
                passwordInput.setError("PLA");
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
