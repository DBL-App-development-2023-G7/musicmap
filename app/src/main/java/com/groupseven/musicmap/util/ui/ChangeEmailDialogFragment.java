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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.regex.InputChecker;

public class ChangeEmailDialogFragment extends DialogFragment {

    private EditText newEmailInput;
    private EditText passwordInput;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.change_email_dialog, null);
        newEmailInput = dialogView.findViewById(R.id.email_dialog_editText);
        passwordInput = dialogView.findViewById(R.id.password_dialog_editText);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog alertDialog = builder.setView(dialogView)
                // Add title to the dialog
                .setTitle(R.string.change_email)
                // Add description message to the dialog
                .setMessage(R.string.email_dialog_description)
                // Add action buttons
                .setPositiveButton(R.string.change_email, null).setNegativeButton(R.string.cancel,
                        (dialog, id) -> this.dismiss()).create();

        // Required so that the dialog won't close when an error occurs after pressing delete account
        alertDialog.setOnShowListener(dialog -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> changeEmail());
        });

        return alertDialog;
    }

    private void changeEmail() {
        String newEmail = newEmailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (InputChecker.checkEmail(newEmail, newEmailInput) || InputChecker.checkPassword(password, passwordInput)) {
            AuthSystem.updateEmail(newEmail, password).whenCompleteAsync((unused, throwable) -> {
                if (throwable == null) {
                    this.dismiss();
                    Message.showSuccessMessage(requireActivity(), getString(R.string.change_email_success));
                } else {
                    String message = throwable.getMessage();
                    newEmailInput.setError(message);
                }
            }, ContextCompat.getMainExecutor(requireContext()));
        }
    }
}
