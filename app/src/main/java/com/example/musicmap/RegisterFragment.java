package com.example.musicmap;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
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
    private EditText birthdateInput;
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
        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d(TAG, "Username EditText is not focused");
                    checkUsername(usernameInput.getText().toString());
                }
            }
        });

        firstNameInput = (EditText) getView().findViewById(R.id.firstName_editText);
        lastNameInput = (EditText) getView().findViewById(R.id.lastName_editText);
        emailInput = (EditText) getView().findViewById(R.id.emailRegister_editText);
        passwordInput = (EditText) getView().findViewById(R.id.passwordRegister_editText);
        repeatPasswordInput =
                (EditText) getView().findViewById(R.id.repeatPasswordRegister_editText);

        birthdateInput = (EditText) getView().findViewById(R.id.birthdate_editText);
        birthdateInput.setFocusable(false);
        birthdateInput.setClickable(true);
        birthdateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

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

    private void checkUsername(String username) {
        Query query = firestore.collection("Users").whereEqualTo("username", username);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "usernameQuery:success");

                    if (!task.getResult().isEmpty()) {
                        usernameInput.setError("Username already exists!");
                    }
                } else {
                    Log.d(TAG, "usernameQuery:fail");
                }
            }
        });
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_YEAR);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month++;
                        String date = day + "/" + month + "/" + year;
                        birthdateInput.setText(date);
                    }
                }, year, month, day);

        calendar.add(Calendar.YEAR, -13);
        Date date = calendar.getTime();
        dialog.getDatePicker().setMaxDate(date.getTime());
        dialog.show();
    }

    private void register() {
        boolean valid = true;
        String username = usernameInput.getText().toString();
        String firstName = usernameInput.getText().toString();
        String lastName = usernameInput.getText().toString();
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

        //TODO look for another way of writing this
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