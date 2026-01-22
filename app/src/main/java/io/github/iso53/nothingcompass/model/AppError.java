package io.github.iso53.nothingcompass.model;

import androidx.annotation.StringRes;

import io.github.iso53.nothingcompass.R;

public enum AppError {
    SENSOR_MANAGER_NOT_PRESENT(R.string.sensor_error_message),
    ROTATION_VECTOR_SENSOR_NOT_AVAILABLE(R.string.sensor_error_message),
    MAGNETIC_FIELD_SENSOR_NOT_AVAILABLE(R.string.sensor_error_message),
    LOCATION_MANAGER_NOT_PRESENT(R.string.location_error_message),
    LOCATION_DISABLED(R.string.location_error_message),
    NO_LOCATION_PROVIDER_AVAILABLE(R.string.location_error_message);

    private final int messageId;

    AppError(@StringRes int messageId) {
        this.messageId = messageId;
    }

    @StringRes
    public int getMessageId() {
        return messageId;
    }
}
