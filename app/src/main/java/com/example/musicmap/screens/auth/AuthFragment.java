package com.example.musicmap.screens.auth;

import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.musicmap.R;
import com.example.musicmap.util.regex.ValidationUtil;
import com.google.firebase.auth.FirebaseAuth;

public abstract class AuthFragment extends Fragment {

    private FirebaseAuth auth;

    private AuthActivity activity;

    public FirebaseAuth getAuth() {
        return auth;
    }

    public AuthActivity getAuthActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = (AuthActivity) getActivity();
    }

    protected boolean checkPassword(EditText passwordInput, String password) {
        switch (ValidationUtil.isPasswordValid(password)) {
            case EMPTY:
                passwordInput.setError(getString(R.string.input_error_enter_password));
                return false;
            case FORMAT:
                passwordInput.setError(getString(R.string.input_error_valid_password));
                return false;
            case VALID:
                return true;
            default:
                passwordInput.setError(getString(R.string.input_error_unexpected));
                return false;
        }
    }

}