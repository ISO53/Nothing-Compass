package io.github.iso53.nothingcompass.view;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.iso53.nothingcompass.model.Azimuth;
import io.github.iso53.nothingcompass.model.LocationStatus;
import io.github.iso53.nothingcompass.model.SensorAccuracy;

public class CompassViewModel extends ViewModel {
    private final MutableLiveData<Azimuth> azimuth = new MutableLiveData<>();
    private final MutableLiveData<SensorAccuracy> sensorAccuracy = new MutableLiveData<>(SensorAccuracy.NO_CONTACT);
    private final MutableLiveData<Boolean> trueNorth = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hapticFeedback = new MutableLiveData<>(true);
    private final MutableLiveData<Location> location = new MutableLiveData<>();
    private final MutableLiveData<LocationStatus> locationStatus = new MutableLiveData<>(LocationStatus.NOT_PRESENT);

    public MutableLiveData<Azimuth> getAzimuth() {
        return azimuth;
    }

    public MutableLiveData<SensorAccuracy> getSensorAccuracy() {
        return sensorAccuracy;
    }

    public MutableLiveData<Boolean> getTrueNorth() {
        return trueNorth;
    }

    public MutableLiveData<Boolean> getHapticFeedback() {
        return hapticFeedback;
    }

    public MutableLiveData<Location> getLocation() {
        return location;
    }

    public MutableLiveData<LocationStatus> getLocationStatus() {
        return locationStatus;
    }
}
