package com.groupseven.musicmap.screens.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.groupseven.musicmap.R;
import com.groupseven.musicmap.firebase.Session;
import com.groupseven.musicmap.models.User;
import com.groupseven.musicmap.util.firebase.AuthSystem;
import com.groupseven.musicmap.util.ui.ChangeEmailDialogFragment;
import com.groupseven.musicmap.util.ui.ChangePasswordDialogFragment;
import com.groupseven.musicmap.util.ui.ChangeUsernameDialogFragment;
import com.groupseven.musicmap.util.ui.DeleteAccountDialogFragment;
import com.groupseven.musicmap.util.ui.Message;

import java.text.DateFormat;

public class AccountSettingsFragment extends PreferenceFragmentCompat {

    private AppCompatActivity activity;
    private PreferenceScreen preferenceScreen;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey);
        preferenceScreen = getPreferenceScreen();

        activity = (AppCompatActivity) getActivity();

        if (activity == null) {
            throw new IllegalStateException("The AccountSettingsFragment must be contained in an activity.");
        }

        setupProfilePreferences();
        setupSecurityPreferences();
        setupOtherPreferences();
    }

    private final ActivityResultLauncher<Intent> uploadPictureActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) {
                        return;
                    }

                    Uri photoUri = data.getData();
                    AuthSystem.updateProfilePicture(photoUri).exceptionally(throwable -> {
                        Message.showFailureMessage(activity, throwable.getMessage());
                        return null;
                    });
                }
            });

    private void setupProfilePreferences() throws IllegalStateException {
        PreferenceCategory profileCategory = preferenceScreen.findPreference("profile");

        if (profileCategory == null) {
            throw new IllegalStateException("Could not find the profile preferences category.");
        }

        Preference preferenceUsername = profileCategory.findPreference("username");
        Preference preferenceEmail = profileCategory.findPreference("email");
        Preference preferenceFirstName = profileCategory.findPreference("firstName");
        Preference preferenceLastName = profileCategory.findPreference("lastName");
        Preference preferenceBirthdate = profileCategory.findPreference("birthdate");
        Preference preferenceChangeUsername = profileCategory.findPreference("changeUsername");
        Preference preferenceChangeProfilePicture = profileCategory.findPreference("picture");

        if (preferenceUsername == null || preferenceEmail == null || preferenceFirstName == null
                || preferenceLastName == null || preferenceBirthdate == null
                || preferenceChangeProfilePicture == null || preferenceChangeUsername == null) {
            throw new IllegalStateException("Could not find the children of the profile category.");
        }

        User currentUser = Session.getInstance().getCurrentUser();
        if (Session.getInstance().isUserLoaded()) {
            preferenceUsername.setSummary(currentUser.getData().getUsername());
            preferenceEmail.setSummary(currentUser.getData().getEmail());
            preferenceEmail.setOnPreferenceClickListener(view -> false);
            preferenceFirstName.setSummary(currentUser.getData().getFirstName());
            preferenceLastName.setSummary(currentUser.getData().getLastName());

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            preferenceBirthdate.setSummary(dateFormat.format(currentUser.getData().getBirthdate()));

            preferenceChangeUsername.setOnPreferenceClickListener(view -> {
                ChangeUsernameDialogFragment changeUsernameDialogFragment = new ChangeUsernameDialogFragment();
                changeUsernameDialogFragment.show(activity.getSupportFragmentManager(), "ChangeUsernameDialog");

                return false;
            });

            preferenceChangeProfilePicture.setOnPreferenceClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                uploadPictureActivityResultLauncher.launch(intent);
                return false;
            });
        }
    }

    private void setupSecurityPreferences() throws IllegalStateException {
        PreferenceCategory securityCategory = preferenceScreen.findPreference("security");

        if (securityCategory == null) {
            throw new IllegalStateException("Could not fine the security preferences category.");
        }

        Preference preferenceChangeEmail = securityCategory.findPreference("changeEmail");
        Preference preferenceChangePassword = securityCategory.findPreference("changePassword");

        if (preferenceChangeEmail == null || preferenceChangePassword == null) {
            throw new IllegalStateException("Could not find the children of the security category.");
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
            throw new IllegalStateException("Could not find the other preferences category.");
        }

        Preference preferenceLogout = otherCategory.findPreference("logout");
        Preference preferenceDeleteAccount = otherCategory.findPreference("deleteAccount");

        if (preferenceLogout == null || preferenceDeleteAccount == null) {
            throw new IllegalStateException("Could not find the children of the profile category.");
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