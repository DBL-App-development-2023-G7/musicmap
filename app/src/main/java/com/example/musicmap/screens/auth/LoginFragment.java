package com.example.musicmap.screens.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.musicmap.R;
import com.example.musicmap.util.regex.ValidationUtil;
import com.example.musicmap.util.ui.Message;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends AuthFragment {

    private static final String TAG = "FirebaseLogin";

    private EditText emailInput;
    private EditText passwordInput;
    private ViewGroup viewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        viewGroup = container;
        emailInput = rootView.findViewById(R.id.email_editText);
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
                emailInput.setError("Please enter a email address.");
                return false;
            case FORMAT:
                emailInput.setError("Please enter a valid email address.");
                return false;
            case VALID:
                return true;
            default:
                emailInput.setError("Unexpected input.");
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
                passwordInput.setError("Unexpected input.");
                return false;
        }
    }

    private boolean isInputValid(String email, String password) {
        return checkEmail(email) & checkPassword(password);
    }

    private void login() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (isInputValid(email, password)) {
            this.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(this.getAuthActivity(),
                    task -> {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "loginUser:fail", task.getException());
                            Message.builder()
                                    .setView(viewGroup)
                                    .setText("Could not log in")
                                    .setDuration(Message.LONG_DURATION)
                                    .failure()
                                    .show();
                        }
                    });
        }
    }

    private void loadRegisterFragment() {
        this.getAuthActivity().loadRegisterFragment();
    }

    private void loadRegisterArtistFragment() {
        this.getAuthActivity().loadRegisterArtistFragment();
    }

}