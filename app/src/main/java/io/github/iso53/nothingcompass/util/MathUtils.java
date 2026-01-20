package io.github.iso53.nothingcompass.util;

import android.hardware.GeomagneticField;
import android.hardware.SensorManager;
import android.location.Location;

import io.github.iso53.nothingcompass.model.Azimuth;
import io.github.iso53.nothingcompass.model.DisplayRotation;
import io.github.iso53.nothingcompass.model.RotationVector;

public final class MathUtils {
    private MathUtils() {}

    private static final int AZIMUTH = 0;
    private static final int AXIS_SIZE = 3;
    private static final int ROTATION_MATRIX_SIZE = 9;

    public static Azimuth calculateAzimuth(RotationVector rotationVector, DisplayRotation displayRotation) {
        float[] rotationMatrix = getRotationMatrix(rotationVector);
        float[] remappedRotationMatrix = remapRotationMatrix(rotationMatrix, displayRotation);
        float[] orientationInRadians = SensorManager.getOrientation(remappedRotationMatrix, new float[AXIS_SIZE]);
        float azimuthInRadians = orientationInRadians[AZIMUTH];
        float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
        return new Azimuth(azimuthInDegrees);
    }

    private static float[] getRotationMatrix(RotationVector rotationVector) {
        float[] rotationMatrix = new float[ROTATION_MATRIX_SIZE];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector.toArray());
        return rotationMatrix;
    }

    private static float[] remapRotationMatrix(float[] rotationMatrix, DisplayRotation displayRotation) {
        switch (displayRotation) {
            case ROTATION_90:
                return remapRotationMatrix(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X);
            case ROTATION_180:
                return remapRotationMatrix(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y);
            case ROTATION_270:
                return remapRotationMatrix(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X);
            case ROTATION_0:
            default:
                return remapRotationMatrix(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y);
        }
    }

    private static float[] remapRotationMatrix(float[] rotationMatrix, int newX, int newY) {
        float[] remappedRotationMatrix = new float[ROTATION_MATRIX_SIZE];
        SensorManager.remapCoordinateSystem(rotationMatrix, newX, newY, remappedRotationMatrix);
        return remappedRotationMatrix;
    }

    public static float getMagneticDeclination(Location location) {
        float latitude = (float) location.getLatitude();
        float longitude = (float) location.getLongitude();
        float altitude = (float) location.getAltitude();
        long time = location.getTime();
        GeomagneticField geomagneticField = new GeomagneticField(latitude, longitude, altitude, time);
        return geomagneticField.getDeclination();
    }

    public static float getClosestNumberFromInterval(float number, float interval) {
        return Math.round(number / interval) * interval;
    }

    public static boolean isAzimuthBetweenTwoPoints(Azimuth azimuth, Azimuth pointA, Azimuth pointB) {
        float aToB = (pointB.getDegrees() - pointA.getDegrees() + 360f) % 360f;
        float aToAzimuth = (azimuth.getDegrees() - pointA.getDegrees() + 360f) % 360f;
        return (aToB <= 180f) != (aToAzimuth > aToB);
    }
}
