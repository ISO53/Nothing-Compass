package io.github.iso53.nothingcompass.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

public class PreferenceStore {
    private static final String TAG = "PreferenceStore";

    private final MutableLiveData<Boolean> trueNorth = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hapticFeedback = new MutableLiveData<>();

    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private final Observer<Boolean> trueNorthObserver;
    private final Observer<Boolean> hapticFeedbackObserver;

    public PreferenceStore(@NonNull Context context, @NonNull Lifecycle lifecycle) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.sharedPreferenceChangeListener = (prefs, key) -> {
            if (key == null)
                return;
            switch (key) {
                case PreferenceConstants.TRUE_NORTH:
                    updateTrueNorth();
                    break;
                case PreferenceConstants.HAPTIC_FEEDBACK:
                    updateHapticFeedback();
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

        updateTrueNorth();
        updateHapticFeedback();

        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                trueNorth.observeForever(trueNorthObserver);
                hapticFeedback.observeForever(hapticFeedbackObserver);

                sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

                trueNorth.removeObserver(trueNorthObserver);
                hapticFeedback.removeObserver(hapticFeedbackObserver);
            }
        });
    }

    public MutableLiveData<Boolean> getTrueNorth() {
        return trueNorth;
    }

    public MutableLiveData<Boolean> getHapticFeedback() {
        return hapticFeedback;
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
}
