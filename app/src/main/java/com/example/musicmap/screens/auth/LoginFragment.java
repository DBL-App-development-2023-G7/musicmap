package com.example.musicmap.screens.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicmap.R;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.regex.ValidationUtil;

public class LoginFragment extends AuthFragment {

    private static final String TAG = "FirebaseLogin";

    private EditText identifierInput;
    private EditText passwordInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

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

    private boolean checkEmail(String email) {
        switch (ValidationUtil.isEmailValid(email)) {
            case EMPTY:
                identifierInput.setError("Please enter a email address.");
                return false;
            case FORMAT:
                identifierInput.setError("Please enter a valid email address.");
                return false;
            case VALID:
                return true;
            default:
                identifierInput.setError("Unexpected input.");
                return false;
        }
    }

    private boolean checkUsername(String username) {
        switch (ValidationUtil.isUsernameValid(username)) {
            case EMPTY:
                identifierInput.setError("Please enter a username.");
                return false;
            case FORMAT:
                identifierInput.setError("Please enter a valid username.");
                return false;
            case VALID:
                return true;
            default:
                identifierInput.setError("Unexpected input.");
                return false;
        }
    }

    private boolean checkPassword(String password) {
        switch (ValidationUtil.isPasswordValid(password)) {
            case EMPTY:
                passwordInput.setError("Please enter a password.");
                return false;
            case FORMAT:
                passwordInput.setError("Please enter a valid password.");
                return false;
            case VALID:
                return true;
            default:
                return false;
        }
    }

    private void login() {
        String identifier = identifierInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (checkPassword(password)) {
            if (checkEmail(identifier)) {
                this.getAuth().signInWithEmailAndPassword(identifier, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "loginUser:success");
                    } else {
                        Log.d(TAG, "loginUser:fail", task.getException());
                        Toast.makeText(getActivity(), "Incorrect email/password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (checkUsername(identifier)) {
                AuthSystem.loginWithUsernameAndPassword(identifier, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "loginUser:success");
                    } else {
                        Log.d(TAG, "loginUser:fail", task.getException());
                        Toast.makeText(getActivity(), "Incorrect username/password.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void loadRegisterFragment() {
        this.getAuthActivity().loadRegisterFragment();
    }

    private void loadRegisterArtistFragment() {
        this.getAuthActivity().loadRegisterArtistFragment();
    }

}