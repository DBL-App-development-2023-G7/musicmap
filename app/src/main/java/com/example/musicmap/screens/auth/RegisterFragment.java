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
import com.example.musicmap.models.UserData;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.regex.InputChecker;
import com.example.musicmap.util.ui.BirthdatePickerDialog;
import com.example.musicmap.util.ui.Message;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class RegisterFragment extends AuthFragment {

    private static final String TAG = "FirebaseRegister";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        usernameInput = rootView.findViewById(R.id.username_editText);
        usernameInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                InputChecker.checkUsername(usernameInput.getText().toString(), usernameInput);
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

    private boolean checkBirthdate(Date birthdate) {
        String birthdateText = birthdateInput.getText().toString();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());

        if (birthdateText.isEmpty()) {
            birthdateInput.setError(getString(R.string.input_error_pick_date));
            return false;
        }
        try {
            Date date = dateFormat.parse(birthdateInput.getText().toString());
            if (date != null) {
                birthdate.setTime(date.getTime());
                birthdateInput.setError(null);
                return true;
            }
            throw new ParseException(getString(R.string.input_exception_date_null), -1);
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
        return InputChecker.checkUsername(username, usernameInput)
                & InputChecker.checkFirstName(firstName, firstNameInput)
                & InputChecker.checkLastName(lastName, lastNameInput)
                & InputChecker.checkEmail(email, emailInput)
                & InputChecker.checkPassword(password, passwordInput)
                & InputChecker.checkRepeatPassword(repeatPassword, password, repeatPasswordInput)
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
                    getString(R.string.auth_error_invalid_values));
            return;
        }

        AuthSystem.register(createUserData(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        this.getAuthActivity().loadHomeActivity();
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e(TAG, "Exception occurred during registration", exception);
                            Message.showFailureMessage(getActivity(), exception.getMessage());
                        } else {
                            Log.e(TAG, "Could not register user");
                            Message.showFailureMessage(getActivity(),
                                    getString(R.string.auth_error_failed_registration));
                        }
                    }
                });
    }

    private void back() {
        this.getAuthActivity().loadLoginFragment();
    }

}