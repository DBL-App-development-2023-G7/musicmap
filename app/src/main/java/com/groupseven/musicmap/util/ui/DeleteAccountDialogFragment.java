package com.groupseven.musicmap.util.ui;

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

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.regex.InputChecker;

public class DeleteAccountDialogFragment extends DialogFragment {

    private EditText passwordInput;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.delete_account_dialog, null);
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
                        (dialog, id) -> this.dismiss()).create();

        // Required so that the dialog won't close when an error occurs after pressing delete account
        alertDialog.setOnShowListener(dialog -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> deleteAccount());
        });

        return alertDialog;
    }

    private void deleteAccount() {
        String password = passwordInput.getText().toString();

        if (InputChecker.checkPassword(password, passwordInput)) {
            AuthSystem.deleteUser(password).exceptionally(throwable -> {
                String message = throwable.getMessage();
                passwordInput.setError(message);
                return null;
            });
        }
    }

}
