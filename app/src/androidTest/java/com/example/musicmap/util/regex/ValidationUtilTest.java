package com.example.musicmap.util.regex;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ValidationUtilTest {

    @Test
    public void isMandatoryFieldValidTest_ResultEmpty() {
        String string = "";
        ValidationUtil.Result result = ValidationUtil.isMandatoryFieldValid(string);
        assertEquals(result, ValidationUtil.Result.EMPTY);
    }

    @Test
    public void isMandatoryFieldValidTest_ResultValid() {
        String string = "AnyString";
        ValidationUtil.Result result = ValidationUtil.isMandatoryFieldValid(string);
        assertEquals(result, ValidationUtil.Result.VALID);
    }

    @Test
    public void isUsernameValidTest_ResultEmpty() {
        String string = "";
        ValidationUtil.Result result = ValidationUtil.isUsernameValid(string);
        assertEquals(result, ValidationUtil.Result.EMPTY);
    }

    @Test
    public void isUsernameValidTest_ResultFormat() {
        String string = "not-matching-format";
        ValidationUtil.Result result = ValidationUtil.isUsernameValid(string);
        assertEquals(result, ValidationUtil.Result.FORMAT);
    }

    @Test
    public void isUsernameValidTest_ResultValid() {
        String string = "matching_format";
        ValidationUtil.Result result = ValidationUtil.isUsernameValid(string);
        assertEquals(result, ValidationUtil.Result.VALID);
    }

    @Test
    public void isIdentifierValidTest_ResultEmpty() {
        String string = "";
        ValidationUtil.Result result = ValidationUtil.isIdentifierValid(string);
        assertEquals(result, ValidationUtil.Result.EMPTY);
    }

    @Test
    public void isIdentifierValidTest_ResultFormat_EmailMismatch() {
        String string = "invalid@email";
        ValidationUtil.Result result = ValidationUtil.isIdentifierValid(string);
        assertEquals(result, ValidationUtil.Result.FORMAT);
    }

    @Test
    public void isIdentifierValidTest_ResultFormat_UsernameMismatch() {
        String string = "invalid-username";
        ValidationUtil.Result result = ValidationUtil.isIdentifierValid(string);
        assertEquals(result, ValidationUtil.Result.FORMAT);
    }

    @Test
    public void isIdentifierValidTest_ResultValid_EmailMatch() {
        String string = "valid@email.com";
        ValidationUtil.Result result = ValidationUtil.isIdentifierValid(string);
        assertEquals(result, ValidationUtil.Result.VALID);
    }

    @Test
    public void isIdentifierValidTest_ResultValid_UsernameMatch() {
        String string = "valid_username";
        ValidationUtil.Result result = ValidationUtil.isIdentifierValid(string);
        assertEquals(result, ValidationUtil.Result.VALID);
    }

    @Test
    public void isEmailValidTest_ResultEmpty() {
        String string = "";
        ValidationUtil.Result result = ValidationUtil.isEmailValid(string);
        assertEquals(result, ValidationUtil.Result.EMPTY);
    }

    @Test
    public void isEmailValidTest_ResultFormat() {
        String string = "invalid.com@email";
        ValidationUtil.Result result = ValidationUtil.isEmailValid(string);
        assertEquals(result, ValidationUtil.Result.FORMAT);
    }

    @Test
    public void isEmailValidTest_ResultValid() {
        String string = "valid@email.com";
        ValidationUtil.Result result = ValidationUtil.isEmailValid(string);
        assertEquals(result, ValidationUtil.Result.VALID);
    }

    @Test
    public void isPasswordValidTest_ResultEmpty() {
        String string = "";
        ValidationUtil.Result result = ValidationUtil.isPasswordValid(string);
        assertEquals(result, ValidationUtil.Result.EMPTY);
    }

    @Test
    public void isPasswordValidTest_ResultFormat() {
        String string = "small";
        ValidationUtil.Result result = ValidationUtil.isPasswordValid(string);
        assertEquals(result, ValidationUtil.Result.FORMAT);
    }

    @Test
    public void isPasswordValidTest_ResultValid() {
        String string = "valid-password";
        ValidationUtil.Result result = ValidationUtil.isPasswordValid(string);
        assertEquals(result, ValidationUtil.Result.VALID);
    }

}
