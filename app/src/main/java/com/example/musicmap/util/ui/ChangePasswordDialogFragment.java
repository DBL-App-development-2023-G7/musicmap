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
import com.example.musicmap.util.regex.InputChecker;

public class ChangePasswordDialogFragment extends DialogFragment {

    private EditText oldPasswordInput;
    private EditText newPasswordInput;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
        oldPasswordInput = dialogView.findViewById(R.id.oldPassword_dialog_editText);
        newPasswordInput = dialogView.findViewById(R.id.newPassword_dialog_editText);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog alertDialog = builder.setView(dialogView)
                // Add title to the dialog
                .setTitle(R.string.change_password)
                // Add description message to the dialog
                .setMessage(R.string.password_dialog_description)
                // Add action buttons
                .setPositiveButton(R.string.change_password, null).setNegativeButton(R.string.cancel,
                        (dialog, id) -> this.dismiss()).create();

        // Required so that the user can update their password
        alertDialog.setOnShowListener(dialog -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> changePassword());
        });

        return alertDialog;
    }

    private void changePassword() {
        String oldPassword = oldPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();

        if (InputChecker.checkPassword(oldPassword, oldPasswordInput) & InputChecker.checkPassword(newPassword,
                newPasswordInput)) {
            AuthSystem.updatePassword(oldPassword, newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    this.dismiss();
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        String message = exception.getMessage();
                        oldPasswordInput.setError(message);
                    }
                }
            });
        }
    }

}
