package io.github.iso53.nothingcompass.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class Azimuth implements Comparable<Azimuth> {
    private final float degrees;

    public Azimuth(float rawDegrees) {
        if (!Float.isFinite(rawDegrees)) {
            throw new IllegalArgumentException("Degrees must be finite but was '" + rawDegrees + "'");
        }
        this.degrees = normalizeAngle(rawDegrees);
    }

    public float getDegrees() {
        return degrees;
    }

    public int getRoundedDegrees() {
        return (int) normalizeAngle(Math.round(degrees));
    }

    @NonNull
    public CardinalDirection getCardinalDirection() {
        if (degrees >= 22.5f && degrees < 67.5f) return CardinalDirection.NORTHEAST;
        if (degrees >= 67.5f && degrees < 112.5f) return CardinalDirection.EAST;
        if (degrees >= 112.5f && degrees < 157.5f) return CardinalDirection.SOUTHEAST;
        if (degrees >= 157.5f && degrees < 202.5f) return CardinalDirection.SOUTH;
        if (degrees >= 202.5f && degrees < 247.5f) return CardinalDirection.SOUTHWEST;
        if (degrees >= 247.5f && degrees < 292.5f) return CardinalDirection.WEST;
        if (degrees >= 292.5f && degrees < 337.5f) return CardinalDirection.NORTHWEST;
        return CardinalDirection.NORTH;
    }

    private float normalizeAngle(float angleInDegrees) {
        return (angleInDegrees + 360f) % 360f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Azimuth azimuth = (Azimuth) o;
        return Float.compare(azimuth.degrees, degrees) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(degrees);
    }

    @Override
    public String toString() {
        return "Azimuth(degrees=" + degrees + ")";
    }

    public Azimuth plus(float degrees) {
        return new Azimuth(this.degrees + degrees);
    }

    public Azimuth minus(float degrees) {
        return new Azimuth(this.degrees - degrees);
    }

    @Override
    public int compareTo(@NonNull Azimuth azimuth) {
        return Float.compare(this.degrees, azimuth.degrees);
    }
}
