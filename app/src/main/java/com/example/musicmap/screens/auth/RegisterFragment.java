package com.example.musicmap.screens.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.musicmap.R;
import com.example.musicmap.user.UserData;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.firebase.Queries;
import com.example.musicmap.util.regex.ValidationUtil;
import com.example.musicmap.util.ui.BirthdatePickerDialog;
import com.example.musicmap.util.ui.Message;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterFragment extends AuthFragment {

    private static final String TAG = "FirebaseRegister";

    private FirebaseFirestore firestore;

    //region declaration of the register form elements
    private EditText usernameInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;
    private EditText birthdateInput;
    //endregion

    //region declaration of the register form elements' values
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String repeatPassword;
    private Date birthdate;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        usernameInput = rootView.findViewById(R.id.username_editText);
        usernameInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                checkUsername(usernameInput.getText().toString());
            }
        });

        firstNameInput = rootView.findViewById(R.id.firstName_editText);
        lastNameInput = rootView.findViewById(R.id.lastName_editText);
        emailInput = rootView.findViewById(R.id.emailRegister_editText);
        passwordInput = rootView.findViewById(R.id.passwordRegister_editText);
        repeatPasswordInput = rootView.findViewById(R.id.repeatPasswordRegister_editText);

        birthdateInput = rootView.findViewById(R.id.birthdate_editText);
        birthdateInput.setFocusable(false);
        birthdateInput.setClickable(true);
        birthdateInput.setOnClickListener(view -> selectDate());

        Button registerButton = rootView.findViewById(R.id.registerRegister_button);
        registerButton.setOnClickListener(view -> register());

        Button backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> back());
        return rootView;
    }

    private void selectDate() {
        DatePickerDialog dialog = new BirthdatePickerDialog(this.getAuthActivity(),
                BirthdatePickerDialog.applyDateToEditText(birthdateInput));
        dialog.show();
    }

    private boolean checkUsername(String username) {
        switch (ValidationUtil.isUsernameValid(username)) {
            case EMPTY:
                usernameInput.setError("Please enter a username.");
                return false;
            case FORMAT:
                usernameInput.setError("Please enter a valid username.");
                return false;
            case VALID:
                Queries.getUsersWithUsername(firestore, username).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "usernameQuery:success");

                        if (!task.getResult().isEmpty()) {
                            usernameInput.setError("Username already exists!");
                        }
                    } else {
                        Log.d(TAG, "usernameQuery:fail");
                    }
                });
                return true;
            default:
                usernameInput.setError("Unexpected input.");
                return false;
        }
    }

    private boolean checkFirstName(String firstName) {
        switch (ValidationUtil.isMandatoryFieldValid(firstName)) {
            case EMPTY:
                firstNameInput.setError("Please enter a First Name.");
                return false;
            case VALID:
                return true;
            default:
                firstNameInput.setError("Unexpected input.");
                return false;
        }
    }

    private boolean checkLastName(String lastName) {
        switch (ValidationUtil.isMandatoryFieldValid(lastName)) {
            case EMPTY:
                lastNameInput.setError("Please enter a Last Name.");
                return false;
            case VALID:
                return true;
            default:
                lastNameInput.setError("Unexpected input.");
                return false;
        }
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

    private boolean checkRepeatPassword(String repeatPassword, String password) {
        if (!repeatPassword.equals(password)) {
            repeatPasswordInput.setError("Passwords do not match!");
            return false;
        }
        repeatPasswordInput.setError(null);
        return true;
    }

    private boolean checkBirthdate(Date birthdate) {
        String birthdateText = birthdateInput.getText().toString();
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (birthdateText.equals("")) {
            birthdateInput.setError("Please pick a date.");
            return false;
        }
        try {
            Date date = dateFormat.parse(birthdateInput.getText().toString());
            if (date != null) {
                birthdate.setTime(date.getTime());
                birthdateInput.setError(null);
                return true;
            }
            throw new ParseException("The date was parsed but the date is null.", -1);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse birthdate.");
            return false;
        }
    }

    private void updateFormValues() {
        username = usernameInput.getText().toString();
        firstName = firstNameInput.getText().toString();
        lastName = lastNameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        repeatPassword = repeatPasswordInput.getText().toString();
        birthdate = new Date();
    }

    /**
     * Validates all {@link #updateFormValues() previously retrieved} form input values.
     *
     * @return whether the form input is valid.
     */
    protected boolean validate() {
        return checkUsername(username)
                & checkFirstName(firstName) & checkLastName(lastName)
                & checkEmail(email)
                & checkPassword(password) & checkRepeatPassword(repeatPassword, password)
                & checkBirthdate(birthdate);
    }

    /**
     * Creates a {@link UserData} instance using the {@link #updateFormValues() previously retrieved}
     * input values of the form.
     *
     * @return the created {@link UserData} instance.
     */
    protected UserData createUserData() {
        return new UserData(username, firstName, lastName, email, birthdate);
    }

    /**
     * Attempts to register the user, by first {@link #updateFormValues() retrieving} the input values,
     * then {@link #validate() validating} them, then registering the {@link #createUserData() user}.
     */
    protected void register() {
        updateFormValues();

        if (!validate()) {
            Message.showFailureMessage(getActivity(),
                    "Some of the fields are incomplete or contain invalid values");
            return;
        }

        AuthSystem.register(createUserData(), password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {

                        if (task.getException() != null) {
                            Message.showFailureMessage(getActivity(),
                                    "Could not register, please try again later");
                            Log.e(TAG, task.getException().toString());
                        }
                    }
                });
    }

    private void back() {
        this.getAuthActivity().loadLoginFragment();
    }

}