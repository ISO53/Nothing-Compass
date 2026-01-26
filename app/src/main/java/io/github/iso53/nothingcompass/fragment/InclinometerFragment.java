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

public class InclinometerFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private InclinometerView inclinometerView;
    private float radius;

    public InclinometerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inclinometer, container, false);
        inclinometerView = v.findViewById(R.id.inclinometerView);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
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

        // Tilt angles mapped to -1 .. +1 range
        float roll = -gx;   // left/right tilt
        float pitch = gy;   // forward/back tilt

        inclinometerView.updateTilt(pitch, roll);
    }
}