package io.github.iso53.nothingcompass;

import io.github.iso53.nothingcompass.BuildConfig;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import io.github.iso53.nothingcompass.preference.PreferenceConstants;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference versionPreference = findPreference(PreferenceConstants.VERSION);
        if (versionPreference != null) {
            versionPreference.setSummary(BuildConfig.VERSION_NAME);
        }

        Preference thirdPartyLicensesPreference = findPreference(PreferenceConstants.THIRD_PARTY_LICENSES);
        if (thirdPartyLicensesPreference != null) {
            thirdPartyLicensesPreference.setOnPreferenceClickListener(preference -> {
                // Third-party licenses not implemented yet
                return true;
            });
        }
    }
}
