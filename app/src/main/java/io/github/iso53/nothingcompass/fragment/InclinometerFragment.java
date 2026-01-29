package io.github.iso53.nothingcompass.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import io.github.iso53.nothingcompass.R;
import io.github.iso53.nothingcompass.view.InclinometerView;
import io.github.iso53.nothingcompass.view.LevelMeterView;

public class InclinometerFragment extends Fragment implements SensorEventListener {

    private static final float TRANSITION_THRESHOLD = 0.5f; // gz threshold for switching views
    private static final long ANIMATION_DURATION = 300; // milliseconds

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private InclinometerView inclinometerView;
    private LevelMeterView levelMeterView;

    private boolean isInclinometerVisible = true;

    public InclinometerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inclinometer, container, false);
        inclinometerView = v.findViewById(R.id.inclinometerView);
        levelMeterView = v.findViewById(R.id.levelMeterView);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        // Initially show inclinometer, hide level meter
        inclinometerView.setAlpha(1f);
        levelMeterView.setAlpha(0f);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        float gx = e.values[0];
        float gy = e.values[1];
        float gz = e.values[2];

        // Normalize gravity vector
        float norm = (float) Math.sqrt(gx * gx + gy * gy + gz * gz);
        gx /= norm;
        gy /= norm;
        gz /= norm;

        // Determine which view should be visible based on device orientation
        // |gz| close to 1 = device is flat (show inclinometer)
        // |gz| close to 0 = device is upright (show level meter)
        boolean shouldShowInclinometer = Math.abs(gz) > TRANSITION_THRESHOLD;

        // Animate transition if orientation changed
        if (shouldShowInclinometer != isInclinometerVisible) {
            isInclinometerVisible = shouldShowInclinometer;
            animateViewTransition(shouldShowInclinometer);
        }

        // Update both views (they'll only draw if visible)
        float roll = -gx; // left/right tilt
        float pitch = gy; // forward/back tilt

        inclinometerView.updateTilt(pitch, roll);
        levelMeterView.updateTilt(gx, gy);
    }

    private void animateViewTransition(boolean showInclinometer) {
        if (showInclinometer) {
            // Fade in inclinometer, fade out level meter
            inclinometerView.animate()
                    .alpha(1f)
                    .setDuration(ANIMATION_DURATION)
                    .start();

            levelMeterView.animate()
                    .alpha(0f)
                    .setDuration(ANIMATION_DURATION)
                    .start();
        } else {
            // Fade out inclinometer, fade in level meter
            inclinometerView.animate()
                    .alpha(0f)
                    .setDuration(ANIMATION_DURATION)
                    .start();

            levelMeterView.animate()
                    .alpha(1f)
                    .setDuration(ANIMATION_DURATION)
                    .start();
        }
    }
}