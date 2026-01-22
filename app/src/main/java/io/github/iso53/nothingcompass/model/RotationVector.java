package io.github.iso53.nothingcompass.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class RotationVector {
    private final float x;
    private final float y;
    private final float z;

    public RotationVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float[] toArray() {
        return new float[] { x, y, z };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RotationVector that = (RotationVector) o;
        return Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0 &&
                Float.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @NonNull
    @Override
    public String toString() {
        return "RotationVector(x=" + x + ", y=" + y + ", z=" + z + ")";
    }
}
