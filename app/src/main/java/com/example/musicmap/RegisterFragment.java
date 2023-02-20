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

public class RegisterFragment extends Fragment {

    private static final String TAG = "FirebaseRegister";
    private FirebaseAuth auth;

    private EditText emailInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;

    public RegisterFragment() {
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
        usernameInput = (EditText) getView().findViewById(R.id.username_editText);
        emailInput = (EditText) getView().findViewById(R.id.email_editText);
        passwordInput = (EditText) getView().findViewById(R.id.password_editText);
        confirmPasswordInput = (EditText) getView().findViewById(R.id.repeatPasswordRegister_editText);

//        registerButton = (Button) getView().findViewById(R.id.register_button);
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                register();
//            }
//        });
    }

    public void register() {
        boolean valid = true;
        String email = emailInput.getText().toString();

        if (email.equals("")) {
            emailInput.setError("");
            valid = false;
        }

        String password = passwordInput.getText().toString();

        if (password.equals("")) {
            passwordInput.setError("");
        }

        if (email.equals("") || password.equals("")) {
            Log.w(TAG, "createUserWithEmail:failure");
            Toast.makeText(getActivity(), "Please enter a valid email and a password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (valid) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUser:success");
                        auth.signOut();
                    } else {
                        Log.w(TAG, "createUser:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}