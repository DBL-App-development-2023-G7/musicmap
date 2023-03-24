package com.example.musicmap.screens.auth;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.musicmap.R;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.regex.MMPatterns;
import com.example.musicmap.util.regex.ValidationUtil;
import com.example.musicmap.util.ui.Message;

public class LoginFragment extends AuthFragment {

    private static final String TAG = "FirebaseLogin";

    private EditText identifierInput;
    private EditText passwordInput;
    private ViewGroup viewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        viewGroup = container;
        identifierInput = rootView.findViewById(R.id.identifier_editText);
        passwordInput = rootView.findViewById(R.id.password_editText);

        Button loginButton = rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> login());

        Button registerButton = rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(view -> loadRegisterFragment());

        Button registerArtistButton = rootView.findViewById(R.id.registerArtist_button);
        registerArtistButton.setOnClickListener(view -> loadRegisterArtistFragment());

        return rootView;
    }

    private boolean checkIdentifier(String identifier) {
        switch (ValidationUtil.isIdentifierValid(identifier)) {
            case EMPTY:
                identifierInput.setError(getString(R.string.input_error_enter_email));
                return false;
            case FORMAT:
                identifierInput.setError(getString(R.string.input_error_valid_email));
                return false;
            case VALID:
                return true;
            default:
                identifierInput.setError(getString(R.string.input_error_unexpected));
                return false;
        }
    }

    protected boolean validate(String identifier, String password) {
        return checkIdentifier(identifier) & checkPassword(passwordInput, password);
    }

    private void login() {
        String identifier = identifierInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (validate(identifier, password)) {
            if (Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                this.getAuth().signInWithEmailAndPassword(identifier, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "loginUser:success");
                        this.getAuthActivity().loadHomeActivity();
                    } else {
                        Log.d(TAG, "loginUser:fail", task.getException());
                        Message.showFailureMessage(viewGroup,
                                getString(R.string.auth_error_incorrect_email_password));
                    }
                });
            } else if (MMPatterns.USERNAME.matcher(identifier).matches()) {
                AuthSystem.loginWithUsernameAndPassword(identifier, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "loginUser:success");
                        this.getAuthActivity().loadHomeActivity();
                    } else {
                        Log.d(TAG, "loginUser:fail", task.getException());
                        Message.showFailureMessage(viewGroup,
                                getString(R.string.auth_error_incorrect_username_password));
                    }
                });
            }
        } else {
            Message.showFailureMessage(viewGroup, getString(R.string.auth_error_invalid_values));
        }
    }

    private void loadRegisterFragment() {
        this.getAuthActivity().loadRegisterFragment();
    }

    private void loadRegisterArtistFragment() {
        this.getAuthActivity().loadRegisterArtistFragment();
    }

}