package com.groupseven.musicmap.util.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.regex.InputChecker;

public class ChangeUsernameDialogFragment extends DialogFragment {

    private static final String TAG = "ChangerUsernameDialogFormat";

    private EditText newUsernameInput;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.change_username_dialog, null);
        newUsernameInput = dialogView.findViewById(R.id.username_dialog_editText);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog alertDialog = builder.setView(dialogView)
                // Add title to the dialog
                .setTitle(R.string.change_username)
                // Add description message to the dialog
                .setMessage(R.string.username_dialog_description)
                // Add action buttons
                .setPositiveButton(R.string.change_username, null).setNegativeButton(R.string.cancel,
                        (dialog, id) -> this.dismiss()).create();

        // Required so that the dialog won't close when an error occurs after pressing delete account
        alertDialog.setOnShowListener(dialog -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> changeUsername());
        });

        return alertDialog;
    }

    private void changeUsername() {
        String newUsername = newUsernameInput.getText().toString();

        if (InputChecker.checkUsername(newUsername, newUsernameInput)) {
            AuthSystem.updateUsername(newUsername).whenCompleteAsync((unused, throwable) -> {
                if (throwable == null) {
                    this.dismiss();
                } else {
                    Log.e(TAG, "Exception occurred while changing username", throwable);
                    String message = throwable.getMessage();
                    newUsernameInput.setError(message);
                }
            });
        }
    }

}
