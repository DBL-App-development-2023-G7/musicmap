package com.example.musicmap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private static final String TAG = "FirebaseLogin";
    private FirebaseAuth auth;

    private EditText emailInput;
    private EditText passwordInput;
    private Button registerButton;
    private Button loginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        emailInput = (EditText) getView().findViewById(R.id.email_editText);
        passwordInput = (EditText) getView().findViewById(R.id.password_editText);

        registerButton = (Button) getView().findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        loginButton = (Button) getView().findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void register() {
        AuthActivity authActivity = (AuthActivity) getActivity();
        authActivity.loadRegister();
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
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "registerUser:success");
                        FirebaseUser user = auth.getCurrentUser();
                        AuthActivity authActivity = (AuthActivity) getActivity();
                        authActivity.loadNext();
                    } else {
                        Log.d(TAG, "registerUser:fail", task.getException());
                        Toast.makeText(getActivity(), "Incorrect email/password.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}