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
            AuthSystem.updatePassword(oldPassword, newPassword)
                    .whenCompleteAsync((unused, throwable) -> {
                        if (throwable == null) {
                            this.dismiss();
                            Message.showSuccessMessage(requireActivity(), getString(R.string.change_password_success));
                        } else {
                            String message = throwable.getMessage();
                            oldPasswordInput.setError(message);
                        }
                    }, ContextCompat.getMainExecutor(requireContext()));
        }
    }

}
