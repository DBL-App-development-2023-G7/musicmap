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

public class LoginFragment extends AuthFragment {

    private static final String TAG = "FirebaseLogin";
    private EditText emailInput;
    private EditText passwordInput;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

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

    private void login() {
        boolean valid = true;

        String email = emailInput.getText().toString();
        if (email.equals("")) {
            emailInput.setError("Enter a valid email");
            valid = false;
        }

        String password = passwordInput.getText().toString();
        if (password.equals("")) {
            passwordInput.setError("Enter a valid password");
            valid = false;
        }

        if (valid) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity,
                    task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "registerUser:success");
                            activity.loadNext();
                        } else {
                            Log.d(TAG, "registerUser:fail", task.getException());
                            Toast.makeText(getActivity(), "Incorrect email/password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadRegisterFragment() {
        activity.loadRegisterFragment();
    }

    private void loadRegisterArtistFragment() {
        activity.loadRegisterArtistFragment();
    }
}