package io.github.iso53.nothingcompass.model;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import io.github.iso53.nothingcompass.R;

public enum SensorAccuracy {
    NO_CONTACT(
            R.string.sensor_accuracy_no_contact,
            R.drawable.ic_sensor_no_contact,
            androidx.appcompat.R.attr.colorError
    ),
    UNRELIABLE(
            R.string.sensor_accuracy_unreliable,
            R.drawable.ic_sensor_unreliable,
            androidx.appcompat.R.attr.colorError
    ),
    LOW(
            R.string.sensor_accuracy_low,
            R.drawable.ic_sensor_low,
            androidx.appcompat.R.attr.colorError
    ),
    MEDIUM(
            R.string.sensor_accuracy_medium,
            R.drawable.ic_sensor_medium,
            androidx.appcompat.R.attr.colorError
    ),
    HIGH(
            R.string.sensor_accuracy_high,
            R.drawable.ic_sensor_high,
            androidx.appcompat.R.attr.colorControlNormal
    );

    private final int textResourceId;
    private final int iconResourceId;
    private final int iconTintAttributeResourceId;

    SensorAccuracy(@StringRes int textResourceId, @DrawableRes int iconResourceId, @AttrRes int iconTintAttributeResourceId) {
        this.textResourceId = textResourceId;
        this.iconResourceId = iconResourceId;
        this.iconTintAttributeResourceId = iconTintAttributeResourceId;
    }

    @StringRes
    public int getTextResourceId() {
        return textResourceId;
    }

    @DrawableRes
    public int getIconResourceId() {
        return iconResourceId;
    }

    @AttrRes
    public int getIconTintAttributeResourceId() {
        return iconTintAttributeResourceId;
    }
}
