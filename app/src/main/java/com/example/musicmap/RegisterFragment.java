package com.example.musicmap;

import android.net.Uri;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private static final String TAG = "FirebaseRegister";
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private EditText usernameInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;
    private Button registerButton;
    private Button backButton;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        usernameInput = (EditText) getView().findViewById(R.id.username_editText);
        firstNameInput = (EditText) getView().findViewById(R.id.firstName_editText);
        lastNameInput = (EditText) getView().findViewById(R.id.lastName_editText);
        emailInput = (EditText) getView().findViewById(R.id.emailRegister_editText);
        passwordInput = (EditText) getView().findViewById(R.id.passwordRegister_editText);
        repeatPasswordInput =
                (EditText) getView().findViewById(R.id.repeatPasswordRegister_editText);

        registerButton = (Button) getView().findViewById(R.id.registerRegister_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        backButton = (Button) getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void register() {
        boolean valid = true;
        String email = emailInput.getText().toString();

        if (email.equals("")) {
            emailInput.setError("");
            valid = false;
        }

        String password = passwordInput.getText().toString();

        if (password.equals("")) {
            passwordInput.setError("");
            valid = false;
        }

        String repeatPassword = repeatPasswordInput.getText().toString();
        if (!repeatPassword.equals(password)) {
            repeatPasswordInput.setError("");
            valid = false;
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

                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            addUser(user);
                        }

                    } else {
                        Log.w(TAG, "createUser:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addUser(FirebaseUser user) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", usernameInput.getText().toString());
        data.put("firstName", firstNameInput.getText().toString());
        data.put("lastName", lastNameInput.getText().toString());
        data.put("artist", false);

        firestore.collection("Users").document(user.getUid()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUser:success");
                    sendEmailVerification(user);
                } else {
                    Log.e(TAG, "createUser:failure", task.getException());
                }
            }
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "sentEmailVerification:success");
                    setupProfile(user);
                } else {
                    Log.d(TAG, "sentEmailVerification:failed", task.getException());
                }
            }
        });
    }

    private void setupProfile(FirebaseUser user) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(
                "").setPhotoUri(Uri.parse("")).build();
        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "updateProfile:success");
                } else {
                    Log.d(TAG, "updateProfile:failed", task.getException());
                }
                auth.signOut();
            }
        });
    }

    private void back() {
        AuthActivity authActivity = (AuthActivity) getActivity();
        authActivity.loadLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}