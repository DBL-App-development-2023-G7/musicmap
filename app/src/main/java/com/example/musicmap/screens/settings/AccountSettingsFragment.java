package com.example.musicmap.screens.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.musicmap.R;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.DeleteAccountDialogFragment;

public class AccountSettingsFragment extends PreferenceFragmentCompat {

    private AppCompatActivity activity;
    private PreferenceScreen preferenceScreen;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey);
        preferenceScreen = getPreferenceScreen();

        activity = (AppCompatActivity) getActivity();

        if (activity == null) {
            throw new IllegalArgumentException("The AccountSettingsFragment must be contained in an activity.");
        }

        setupProfilePreferences();
        setupOtherPreferences();
    }

    private void setupOtherPreferences() throws IllegalStateException {
        PreferenceCategory otherCategory = preferenceScreen.findPreference("other");

        if (otherCategory == null) {
            throw new IllegalArgumentException("Could not find the other preferences category.");
        }

        Preference preferenceLogout = otherCategory.findPreference("logout");
        Preference preferenceDeleteAccount = otherCategory.findPreference("deleteAccount");

        if (preferenceLogout == null || preferenceDeleteAccount == null) {
            throw new IllegalArgumentException("Could not find the children of the profile category.");
        }

        preferenceLogout.setOnPreferenceClickListener(preference -> {
            AuthSystem.logout();
            return false;
        });

        preferenceDeleteAccount.setOnPreferenceClickListener(preference -> {
            DeleteAccountDialogFragment deleteAccountDialog = new DeleteAccountDialogFragment();
            deleteAccountDialog.show(activity.getSupportFragmentManager(), "DeleteAccountDialogue");
            return false;
        });
    }

    private void setupProfilePreferences() throws IllegalStateException {
        PreferenceCategory profileCategory = preferenceScreen.findPreference("profile");

        if (profileCategory == null) {
            throw new IllegalArgumentException("Could not find the profile preferences category.");
        }

        Preference preferenceUsername = profileCategory.findPreference("username");
        Preference preferenceEmail = profileCategory.findPreference("email");
        Preference preferenceFirstName = profileCategory.findPreference("firstName");
        Preference preferenceLastName = profileCategory.findPreference("lastName");

        if (preferenceUsername == null || preferenceEmail == null || preferenceFirstName == null
                || preferenceLastName == null) {
            throw new IllegalArgumentException("Could not find the children of the profile category.");
        }

        User currentUser = Session.getInstance().getCurrentUser();
        if (Session.getInstance().isUserLoaded()) {
            preferenceUsername.setSummary(currentUser.getData().getUsername());
            preferenceEmail.setSummary(currentUser.getData().getEmail());
            preferenceFirstName.setSummary(currentUser.getData().getFirstName());
            preferenceLastName.setSummary(currentUser.getData().getLastName());
        }
    }

}