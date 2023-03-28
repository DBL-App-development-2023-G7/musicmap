package com.example.musicmap.screens.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.musicmap.R;
import com.example.musicmap.user.Session;
import com.example.musicmap.user.User;
import com.example.musicmap.util.firebase.AuthSystem;
import com.example.musicmap.util.ui.ChangeEmailDialogFragment;
import com.example.musicmap.util.ui.ChangePasswordDialogFragment;
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
        setupSecurityPreferences();
        setupOtherPreferences();
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
        Preference preferenceBirthdate = profileCategory.findPreference("birthdate");
        Preference preferenceChangeProfilePicture = profileCategory.findPreference("picture");

        if (preferenceUsername == null || preferenceEmail == null || preferenceFirstName == null
                || preferenceLastName == null || preferenceBirthdate == null
                || preferenceChangeProfilePicture == null) {
            throw new IllegalArgumentException("Could not find the children of the profile category.");
        }

        User currentUser = Session.getInstance().getCurrentUser();
        if (Session.getInstance().isUserLoaded()) {
            preferenceUsername.setSummary(currentUser.getData().getUsername());
            preferenceEmail.setSummary(currentUser.getData().getEmail());
            preferenceEmail.setOnPreferenceClickListener(view -> false);
            preferenceFirstName.setSummary(currentUser.getData().getFirstName());
            preferenceLastName.setSummary(currentUser.getData().getLastName());

            //TODO this has to be moved
            java.text.DateFormat dateFormat = DateFormat.getDateFormat(getContext());
            preferenceBirthdate.setSummary(dateFormat.format(currentUser.getData().getBirthdate()));

            preferenceChangeProfilePicture.setOnPreferenceClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                someActivityResultLauncher.launch(intent);
                return false;
            });
        }
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) {
                        return;
                    }

                    Uri photoUri = data.getData();
                    AuthSystem.updateProfilePicture(photoUri);
                }
            });

    private void setupSecurityPreferences() throws IllegalStateException {
        PreferenceCategory securityCategory = preferenceScreen.findPreference("security");

        if (securityCategory == null) {
            throw new IllegalStateException("Could not fine the security preferences category.");
        }

        Preference preferenceChangeEmail = securityCategory.findPreference("changeEmail");
        Preference preferenceChangePassword = securityCategory.findPreference("changePassword");

        if (preferenceChangeEmail == null || preferenceChangePassword == null) {
            throw new IllegalArgumentException("Could not find the children of the security category.");
        }

        preferenceChangeEmail.setOnPreferenceClickListener(view -> {
            ChangeEmailDialogFragment changeEmailDialogFragment = new ChangeEmailDialogFragment();
            changeEmailDialogFragment.show(activity.getSupportFragmentManager(), "ChangeEmailDialog");
            return false;
        });

        preferenceChangePassword.setOnPreferenceClickListener(view -> {
            ChangePasswordDialogFragment changePasswordDialogFragment = new ChangePasswordDialogFragment();
            changePasswordDialogFragment.show(activity.getSupportFragmentManager(), "ChangePasswordDialog");
            return false;
        });
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
            deleteAccountDialog.show(activity.getSupportFragmentManager(), "DeleteAccountDialog");
            return false;
        });
    }

}