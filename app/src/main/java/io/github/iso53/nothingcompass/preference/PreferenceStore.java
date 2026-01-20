package io.github.iso53.nothingcompass.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import io.github.iso53.nothingcompass.model.AppNightMode;

public class PreferenceStore {
    private static final String TAG = "PreferenceStore";

    private final MutableLiveData<Boolean> trueNorth = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hapticFeedback = new MutableLiveData<>();
    private final MutableLiveData<AppNightMode> nightMode = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accessLocationPermissionRequested = new MutableLiveData<>();

    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private final Observer<Boolean> trueNorthObserver;
    private final Observer<Boolean> hapticFeedbackObserver;
    private final Observer<AppNightMode> nightModeObserver;
    private final Observer<Boolean> accessLocationPermissionRequestedObserver;

    public PreferenceStore(@NonNull Context context, @NonNull Lifecycle lifecycle) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.sharedPreferenceChangeListener = (prefs, key) -> {
            if (key == null) return;
            switch (key) {
                case PreferenceConstants.TRUE_NORTH:
                    updateTrueNorth();
                    break;
                case PreferenceConstants.HAPTIC_FEEDBACK:
                    updateHapticFeedback();
                    break;
                case PreferenceConstants.NIGHT_MODE:
                    updateNightMode();
                    break;
                case PreferenceConstants.ACCESS_LOCATION_PERMISSION_REQUESTED:
                    updateAccessLocationPermissionRequested();
                    break;
            }
        };

        this.trueNorthObserver = value -> {
            sharedPreferences.edit().putBoolean(PreferenceConstants.TRUE_NORTH, value).apply();
            Log.d(TAG, "Persisted trueNorth: " + value);
        };

        this.hapticFeedbackObserver = value -> {
            sharedPreferences.edit().putBoolean(PreferenceConstants.HAPTIC_FEEDBACK, value).apply();
            Log.d(TAG, "Persisted hapticFeedback: " + value);
        };


        this.nightModeObserver = value -> {
            sharedPreferences.edit().putString(PreferenceConstants.NIGHT_MODE, value.getPreferenceValue()).apply();
            Log.d(TAG, "Persisted nightMode: " + value);
        };

        this.accessLocationPermissionRequestedObserver = value -> {
            sharedPreferences.edit().putBoolean(PreferenceConstants.ACCESS_LOCATION_PERMISSION_REQUESTED, value).apply();
            Log.d(TAG, "Persisted accessLocationPermissionRequested: " + value);
        };

        updateTrueNorth();
        updateHapticFeedback();
        updateNightMode();
        updateAccessLocationPermissionRequested();

        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                trueNorth.observeForever(trueNorthObserver);
                hapticFeedback.observeForever(hapticFeedbackObserver);
                nightMode.observeForever(nightModeObserver);
                accessLocationPermissionRequested.observeForever(accessLocationPermissionRequestedObserver);

                sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

                trueNorth.removeObserver(trueNorthObserver);
                hapticFeedback.removeObserver(hapticFeedbackObserver);
                nightMode.removeObserver(nightModeObserver);
                accessLocationPermissionRequested.removeObserver(accessLocationPermissionRequestedObserver);
            }
        });
    }

    public MutableLiveData<Boolean> getTrueNorth() {
        return trueNorth;
    }

    public MutableLiveData<Boolean> getHapticFeedback() {
        return hapticFeedback;
    }


    public MutableLiveData<AppNightMode> getNightMode() {
        return nightMode;
    }

    public MutableLiveData<Boolean> getAccessLocationPermissionRequested() {
        return accessLocationPermissionRequested;
    }

    private void updateTrueNorth() {
        boolean storedValue = sharedPreferences.getBoolean(PreferenceConstants.TRUE_NORTH, false);
        if (!Boolean.valueOf(storedValue).equals(trueNorth.getValue())) {
            trueNorth.setValue(storedValue);
        }
    }

    private void updateHapticFeedback() {
        boolean storedValue = sharedPreferences.getBoolean(PreferenceConstants.HAPTIC_FEEDBACK, true);
        if (!Boolean.valueOf(storedValue).equals(hapticFeedback.getValue())) {
            hapticFeedback.setValue(storedValue);
        }
    }


    private void updateNightMode() {
        String storedValue = sharedPreferences.getString(PreferenceConstants.NIGHT_MODE, AppNightMode.FOLLOW_SYSTEM.getPreferenceValue());
        AppNightMode storedNightMode = AppNightMode.forPreferenceValue(storedValue);
        if (nightMode.getValue() != storedNightMode) {
            nightMode.setValue(storedNightMode);
        }
    }

    private void updateAccessLocationPermissionRequested() {
        boolean storedValue = sharedPreferences.getBoolean(PreferenceConstants.ACCESS_LOCATION_PERMISSION_REQUESTED, false);
        if (!Boolean.valueOf(storedValue).equals(accessLocationPermissionRequested.getValue())) {
            accessLocationPermissionRequested.setValue(storedValue);
        }
    }
}
