package io.github.iso53.nothingcompass.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import io.github.iso53.nothingcompass.preference.PreferenceConstants;

public enum AppNightMode {
    FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, PreferenceConstants.NIGHT_MODE_VALUE_FOLLOW_SYSTEM),
    NO(AppCompatDelegate.MODE_NIGHT_NO, PreferenceConstants.NIGHT_MODE_VALUE_NO),
    YES(AppCompatDelegate.MODE_NIGHT_YES, PreferenceConstants.NIGHT_MODE_VALUE_YES);

    private final int systemValue;
    private final String preferenceValue;

    AppNightMode(int systemValue, String preferenceValue) {
        this.systemValue = systemValue;
        this.preferenceValue = preferenceValue;
    }

    public int getSystemValue() {
        return systemValue;
    }

    public String getPreferenceValue() {
        return preferenceValue;
    }

    @NonNull
    public static AppNightMode forPreferenceValue(String preferenceValue) {
        if (PreferenceConstants.NIGHT_MODE_VALUE_NO.equals(preferenceValue)) {
            return NO;
        } else if (PreferenceConstants.NIGHT_MODE_VALUE_YES.equals(preferenceValue)) {
            return YES;
        } else {
            return FOLLOW_SYSTEM;
        }
    }
}
