package com.groupseven.musicmap.screens.auth;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.regex.InputChecker;
import com.groupseven.musicmap.util.regex.MMPatterns;
import com.groupseven.musicmap.util.ui.Message;

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

    protected boolean validate(String identifier, String password) {
        return InputChecker.checkIdentifier(identifier, identifierInput)
                & InputChecker.checkPassword(password, passwordInput);
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
                AuthSystem.loginWithUsernameAndPassword(identifier, password).whenCompleteAsync((authResult, throwable) -> {
                    if (throwable == null) {
                        Log.d(TAG, "loginUser:success");
                        this.getAuthActivity().loadHomeActivity();
                    } else {
                        Log.d(TAG, "loginUser:fail", throwable);
                        Message.showFailureMessage(viewGroup,
                                getString(R.string.auth_error_incorrect_username_password));
                    }
                }, ContextCompat.getMainExecutor(requireContext()));
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