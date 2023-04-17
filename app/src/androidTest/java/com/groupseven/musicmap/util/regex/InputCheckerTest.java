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

    // Similar tests for checkIdentifier, checkPassword, checkFirstName, checkLastName, and checkRepeatPassword

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

}

