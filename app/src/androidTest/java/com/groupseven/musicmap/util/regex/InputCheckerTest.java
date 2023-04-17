package com.groupseven.musicmap.util.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.TestDataStore;
import com.groupseven.musicmap.models.User;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InputCheckerTest {

    private Context context;

    @Before
    public void setup() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void checkEmail_emptyInput() {
        EditText emailInput = new EditText(context);
        emailInput.setText("");
        boolean result = InputChecker.checkEmail("", emailInput);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_enter_email), emailInput.getError());
    }

    @Test
    public void checkEmail_invalidFormat() {
        EditText emailInput = new EditText(context);
        emailInput.setText("invalid-email");
        boolean result = InputChecker.checkEmail("invalid-email", emailInput);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_valid_email), emailInput.getError());
    }

    @Test
    public void checkEmail_validInput() {
        EditText emailInput = new EditText(context);
        emailInput.setText("valid-email@example.com");
        boolean result = InputChecker.checkEmail("valid-email@example.com", emailInput);
        assertTrue(result);
        assertNull(emailInput.getError());
    }

    @Test
    public void checkUsername_emptyInput() throws ExecutionException, InterruptedException {
        EditText usernameInput = new EditText(context);
        usernameInput.setText("");
        CompletableFuture<Boolean> result = InputChecker.checkUsername("", usernameInput);
        boolean check = result.get();
        assertFalse(check);
        assertEquals(context.getString(R.string.input_error_enter_username), usernameInput.getError());
    }

    @Test
    public void checkUsername_invalidFormat() throws ExecutionException, InterruptedException {
        EditText usernameInput = new EditText(context);
        usernameInput.setText("invalid-username");
        CompletableFuture<Boolean> result = InputChecker.checkUsername("invalid-username", usernameInput);
        boolean check = result.get();
        assertFalse(check);
        assertEquals(context.getString(R.string.input_error_valid_username), usernameInput.getError());
    }

    @Test
    public void checkUsername_usernameExists() throws ExecutionException, InterruptedException {
        EditText usernameInput = new EditText(context);
        usernameInput.setText(TestDataStore.USERNAME_THAT_EXISTS_IN_FIREBASE);
        CompletableFuture<User> future = new CompletableFuture<>();
        future.complete(TestDataStore.getValidUser());
        CompletableFuture<Boolean> result = InputChecker.checkUsername(TestDataStore.USERNAME_THAT_EXISTS_IN_FIREBASE,
                usernameInput);
        boolean check = result.get();
        assertFalse(check);
        assertEquals(context.getString(R.string.input_error_username_exists), usernameInput.getError());
    }

    @Test
    public void testCheckIdentifier_empty() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkIdentifier("", input);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_enter_email), input.getError().toString());
    }

    @Test
    public void testCheckIdentifier_invalidFormat() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkIdentifier("invalid-username", input);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_valid_email), input.getError().toString());
    }

    @Test
    public void testCheckIdentifier_valid() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkIdentifier("valid-email@example.com", input);
        assertTrue(result);
        assertNull(input.getError());
    }

    @Test
    public void testCheckPassword_empty() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkPassword("", input);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_enter_password), input.getError().toString());
    }

    @Test
    public void testCheckPassword_invalidFormat() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkPassword("short", input);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_valid_password), input.getError().toString());
    }

    @Test
    public void testCheckPassword_valid() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkPassword("validpassword123", input);
        assertTrue(result);
        assertNull(input.getError());
    }

    @Test
    public void testCheckFirstName_empty() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkFirstName("", input);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_enter_first_name), input.getError().toString());
    }

    @Test
    public void testCheckFirstName_valid() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkFirstName("John", input);
        assertTrue(result);
        assertNull(input.getError());
    }

    @Test
    public void testCheckLastName_empty() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkLastName("", input);
        assertFalse(result);
        assertEquals(context.getString(R.string.input_error_enter_last_name), input.getError().toString());
    }

    @Test
    public void testCheckLastName_valid() {
        EditText input = new EditText(context);
        boolean result = InputChecker.checkLastName("Doe", input);
        assertTrue(result);
        assertNull(input.getError());
    }

    @Test
    public void testCheckRepeatPassword_matchingPasswords() {
        String password = "password123";
        String repeatPassword = "password123";
        EditText repeatPasswordInput = new EditText(context);
        assertTrue(InputChecker.checkRepeatPassword(repeatPassword, password, repeatPasswordInput));
        assertNull(repeatPasswordInput.getError());
    }

    @Test
    public void testCheckRepeatPassword_nonMatchingPasswords() {
        String password = "password123";
        String repeatPassword = "password456";
        EditText repeatPasswordInput = new EditText(context);
        assertFalse(InputChecker.checkRepeatPassword(repeatPassword, password, repeatPasswordInput));
        assertEquals(context.getString(R.string.input_error_passwords_not_matching), repeatPasswordInput.getError().toString());
    }

}

