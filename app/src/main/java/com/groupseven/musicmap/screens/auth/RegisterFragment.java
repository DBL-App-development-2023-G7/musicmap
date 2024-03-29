package com.groupseven.musicmap.screens.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.models.UserData;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.regex.InputChecker;
import com.groupseven.musicmap.util.ui.BirthdatePickerDialog;
import com.groupseven.musicmap.util.ui.Message;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

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
     * @return a future that checks whether the form inputs are valid.
     */
    protected CompletableFuture<Boolean> validate() {
        CompletableFuture<Boolean> checkUsernameFuture = InputChecker.checkUsername(username, usernameInput);

        boolean valid = InputChecker.checkFirstName(firstName, firstNameInput);
        valid &= InputChecker.checkLastName(lastName, lastNameInput);
        valid &= InputChecker.checkEmail(email, emailInput);
        valid &= InputChecker.checkPassword(password, passwordInput);
        valid &= InputChecker.checkRepeatPassword(repeatPassword, password, repeatPasswordInput);
        valid &= checkBirthdate(birthdate);

        if (!valid) {
            return CompletableFuture.completedFuture(false);
        }

        return checkUsernameFuture;
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

        validate().thenAcceptAsync(valid -> {
            if (!valid) {
                Message.showFailureMessage(getActivity(),
                        getString(R.string.auth_error_invalid_values));
                return;
            }

            AuthSystem.register(createUserData(), password)
                    .whenCompleteAsync((unused, throwable) -> {
                        if (throwable == null) {
                            this.getAuthActivity().loadHomeActivity();
                        } else {
                            Log.e(TAG, "Exception occurred during registration", throwable);
                            Message.showFailureMessage(getActivity(), throwable.getMessage());
                        }
                    }, ContextCompat.getMainExecutor(requireContext()));
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void back() {
        this.getAuthActivity().loadLoginFragment();
    }

}