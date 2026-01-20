package io.github.iso53.nothingcompass;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import io.github.iso53.nothingcompass.databinding.FragmentCompassBinding;
import io.github.iso53.nothingcompass.databinding.SensorAlertDialogViewBinding;
import io.github.iso53.nothingcompass.model.AppError;
import io.github.iso53.nothingcompass.model.Azimuth;
import io.github.iso53.nothingcompass.model.DisplayRotation;
import io.github.iso53.nothingcompass.model.LocationStatus;
import io.github.iso53.nothingcompass.model.RotationVector;
import io.github.iso53.nothingcompass.model.SensorAccuracy;
import io.github.iso53.nothingcompass.preference.PreferenceStore;
import io.github.iso53.nothingcompass.util.MathUtils;
import io.github.iso53.nothingcompass.view.CompassViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CompassFragment extends Fragment {
    private static final String TAG = "CompassFragment";

    private CompassViewModel compassViewModel;
    private final CompassMenuProvider compassMenuProvider = new CompassMenuProvider();
    private final CompassSensorEventListener compassSensorEventListener = new CompassSensorEventListener();

    private FragmentCompassBinding binding;
    private PreferenceStore preferenceStore;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private CancellationSignal locationRequestCancellationSignal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompassBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compassViewModel = new ViewModelProvider(this).get(CompassViewModel.class);
        
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setModel(compassViewModel);
        binding.locationReloadButton.setOnClickListener(v -> requestLocation());

        preferenceStore = new PreferenceStore(requireContext(), getViewLifecycleOwner().getLifecycle());
        preferenceStore.getTrueNorth().observe(getViewLifecycleOwner(), value -> compassViewModel.getTrueNorth().setValue(value));
        preferenceStore.getHapticFeedback().observe(getViewLifecycleOwner(), value -> compassViewModel.getHapticFeedback().setValue(value));

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        requireActivity().addMenuProvider(compassMenuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensorListener();

        if (Boolean.TRUE.equals(compassViewModel.getTrueNorth().getValue()) && compassViewModel.getLocation().getValue() == null) {
            requestLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(compassSensorEventListener);
        }
        if (locationRequestCancellationSignal != null) {
            locationRequestCancellationSignal.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void registerSensorListener() {
        if (sensorManager == null) {
            showErrorDialog(AppError.SENSOR_MANAGER_NOT_PRESENT);
            return;
        }

        Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(compassSensorEventListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            showErrorDialog(AppError.ROTATION_VECTOR_SENSOR_NOT_AVAILABLE);
        }

        Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticFieldSensor != null) {
            sensorManager.registerListener(compassSensorEventListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            showErrorDialog(AppError.MAGNETIC_FIELD_SENSOR_NOT_AVAILABLE);
        }
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            registerLocationListener();
        } else {
            compassViewModel.getLocationStatus().setValue(LocationStatus.PERMISSION_DENIED);
        }
    }

    private void registerLocationListener() {
        if (locationManager == null) {
            showErrorDialog(AppError.LOCATION_MANAGER_NOT_PRESENT);
            return;
        }

        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            String provider = getBestLocationProvider();
            if (provider != null) {
                requestLocationFromProvider(provider);
            } else {
                showErrorDialog(AppError.NO_LOCATION_PROVIDER_AVAILABLE);
            }
        } else {
            showErrorDialog(AppError.LOCATION_DISABLED);
        }
    }

    private void requestLocationFromProvider(String provider) {
        compassViewModel.getLocationStatus().setValue(LocationStatus.LOADING);

        if (locationRequestCancellationSignal != null) {
            locationRequestCancellationSignal.cancel();
        }
        locationRequestCancellationSignal = new CancellationSignal();

        LocationManagerCompat.getCurrentLocation(
                locationManager,
                provider,
                locationRequestCancellationSignal,
                ContextCompat.getMainExecutor(requireContext()),
                this::setLocation
        );
    }

    private void setLocation(Location location) {
        compassViewModel.getLocation().setValue(location);
        compassViewModel.getLocationStatus().setValue(location == null ? LocationStatus.NOT_PRESENT : LocationStatus.PRESENT);
    }

    private String getBestLocationProvider() {
        List<String> availableProviders = locationManager.getProviders(true);
        List<String> preferredProviders = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            preferredProviders.add(LocationManager.FUSED_PROVIDER);
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            preferredProviders.add(LocationManager.GPS_PROVIDER);
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            preferredProviders.add(LocationManager.NETWORK_PROVIDER);
        }

        for (String provider : preferredProviders) {
            if (availableProviders.contains(provider)) {
                return provider;
            }
        }
        return null;
    }

    private void showErrorDialog(AppError error) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setIcon(R.drawable.ic_error)
                .setMessage(getString(R.string.error_message, getString(error.getMessageId()), error.name()))
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private class CompassMenuProvider implements MenuProvider {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_compass, menu);
            compassViewModel.getSensorAccuracy().observe(getViewLifecycleOwner(), accuracy -> {
                MenuItem item = menu.findItem(R.id.action_sensor_status);
                if (item != null) {
                    item.setIcon(accuracy.getIconResourceId());
                }
            });
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();
            if (id == R.id.action_sensor_status) {
                showSensorStatusPopup();
                return true;
            } else if (id == R.id.action_settings) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        }

        private void showSensorStatusPopup() {
            SensorAlertDialogViewBinding dialogBinding = SensorAlertDialogViewBinding.inflate(getLayoutInflater());
            dialogBinding.setModel(compassViewModel);
            dialogBinding.setLifecycleOwner(getViewLifecycleOwner());

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.sensor_status)
                    .setView(dialogBinding.getRoot())
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private class CompassSensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                updateCompass(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                compassViewModel.getSensorAccuracy().setValue(adaptSensorAccuracy(accuracy));
            }
        }

        private void updateCompass(SensorEvent event) {
            RotationVector rotationVector = new RotationVector(event.values[0], event.values[1], event.values[2]);
            DisplayRotation displayRotation = getDisplayRotation();
            Azimuth magneticAzimuth = MathUtils.calculateAzimuth(rotationVector, displayRotation);

            if (Boolean.TRUE.equals(compassViewModel.getTrueNorth().getValue())) {
                Location location = compassViewModel.getLocation().getValue();
                float declination = location != null ? MathUtils.getMagneticDeclination(location) : 0f;
                compassViewModel.getAzimuth().setValue(magneticAzimuth.plus(declination));
            } else {
                compassViewModel.getAzimuth().setValue(magneticAzimuth);
            }
        }

        private DisplayRotation getDisplayRotation() {
            int rotation = Surface.ROTATION_0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Display display = requireContext().getDisplay();
                if (display != null) rotation = display.getRotation();
            } else {
                rotation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();
            }

            switch (rotation) {
                case Surface.ROTATION_90: return DisplayRotation.ROTATION_90;
                case Surface.ROTATION_180: return DisplayRotation.ROTATION_180;
                case Surface.ROTATION_270: return DisplayRotation.ROTATION_270;
                default: return DisplayRotation.ROTATION_0;
            }
        }

        private SensorAccuracy adaptSensorAccuracy(int accuracy) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE: return SensorAccuracy.UNRELIABLE;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW: return SensorAccuracy.LOW;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: return SensorAccuracy.MEDIUM;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: return SensorAccuracy.HIGH;
                default: return SensorAccuracy.NO_CONTACT;
            }
        }
    }
}
