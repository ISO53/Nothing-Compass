package io.github.iso53.nothingcompass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;

import androidx.annotation.AnyRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import io.github.iso53.nothingcompass.R;
import io.github.iso53.nothingcompass.databinding.CompassViewBinding;
import io.github.iso53.nothingcompass.model.Azimuth;
import io.github.iso53.nothingcompass.util.MathUtils;

public class CompassView extends ConstraintLayout {

    @IdRes
    private final int center = R.id.compass_rose_image;

    private static final float HAPTIC_FEEDBACK_INTERVAL = 2.0f;
    private Azimuth lastHapticFeedbackPoint = null;

    private final CompassViewBinding binding;

    public CompassView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        binding = CompassViewBinding.inflate(layoutInflater, this, true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setVisibility(INVISIBLE);
        updateStatusDegreesTextSize(w * getFloat(R.dimen.status_degrees_text_size_factor));
        updateStatusCardinalDirectionTextSize(w * getFloat(R.dimen.status_cardinal_direction_text_size_factor));
        updateCardinalDirectionTextSize(w * getFloat(R.dimen.cardinal_direction_text_size_factor));
        updateDegreeTextSize(w * getFloat(R.dimen.degree_text_size_factor));
    }

    private void updateStatusDegreesTextSize(float textSize) {
        binding.statusDegreesText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private void updateStatusCardinalDirectionTextSize(float textSize) {
        binding.statusCardinalDirectionText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private void updateCardinalDirectionTextSize(float textSize) {
        binding.cardinalDirectionNorthText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.cardinalDirectionEastText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.cardinalDirectionSouthText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.cardinalDirectionWestText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private void updateDegreeTextSize(float textSize) {
        binding.degree0Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree30Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree60Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree90Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree120Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree150Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree180Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree210Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree240Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree270Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree300Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        binding.degree330Text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void setAzimuth(float value) {
        Azimuth azimuth = new Azimuth(value);

        updateStatusDegreesText(azimuth);
        updateStatusDirectionText(azimuth);

        float rotation = -azimuth.getDegrees();
        rotateCompassRoseImage(rotation);
        rotateCompassRoseTexts(rotation);
        handleHapticFeedback(azimuth);

        setVisibility(VISIBLE);
    }

    private void updateStatusDegreesText(Azimuth azimuth) {
        binding.statusDegreesText.setText(getContext().getString(R.string.degrees, azimuth.getRoundedDegrees()));
    }

    private void updateStatusDirectionText(Azimuth azimuth) {
        binding.statusCardinalDirectionText.setText(getContext().getString(azimuth.getCardinalDirection().getLabelResourceId()));
    }

    private void rotateCompassRoseImage(float rotation) {
        binding.compassRoseImage.setRotation(rotation);
    }

    private void rotateCompassRoseTexts(float rotation) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        rotateCardinalDirectionTexts(constraintSet, rotation);
        rotateDegreeTexts(constraintSet, rotation);

        constraintSet.applyTo(this);
    }

    private void rotateCardinalDirectionTexts(ConstraintSet constraintSet, float rotation) {
        int radius = calculateTextRadius(getFloat(R.dimen.cardinal_direction_text_ratio));

        constraintSet.constrainCircle(R.id.cardinal_direction_north_text, center, radius, rotation);
        constraintSet.constrainCircle(R.id.cardinal_direction_east_text, center, radius, rotation + 90);
        constraintSet.constrainCircle(R.id.cardinal_direction_south_text, center, radius, rotation + 180);
        constraintSet.constrainCircle(R.id.cardinal_direction_west_text, center, radius, rotation + 270);
    }

    private void rotateDegreeTexts(ConstraintSet constraintSet, float rotation) {
        int radius = calculateTextRadius(getFloat(R.dimen.degree_text_ratio));

        constraintSet.constrainCircle(R.id.degree_0_text, center, radius, rotation);
        constraintSet.constrainCircle(R.id.degree_30_text, center, radius, rotation + 30);
        constraintSet.constrainCircle(R.id.degree_60_text, center, radius, rotation + 60);
        constraintSet.constrainCircle(R.id.degree_90_text, center, radius, rotation + 90);
        constraintSet.constrainCircle(R.id.degree_120_text, center, radius, rotation + 120);
        constraintSet.constrainCircle(R.id.degree_150_text, center, radius, rotation + 150);
        constraintSet.constrainCircle(R.id.degree_180_text, center, radius, rotation + 180);
        constraintSet.constrainCircle(R.id.degree_210_text, center, radius, rotation + 210);
        constraintSet.constrainCircle(R.id.degree_240_text, center, radius, rotation + 240);
        constraintSet.constrainCircle(R.id.degree_270_text, center, radius, rotation + 270);
        constraintSet.constrainCircle(R.id.degree_300_text, center, radius, rotation + 300);
        constraintSet.constrainCircle(R.id.degree_330_text, center, radius, rotation + 330);
    }

    private float getFloat(@AnyRes int id) {
        TypedValue tempValue = new TypedValue();
        getResources().getValue(id, tempValue, true);
        return tempValue.getFloat();
    }

    private int calculateTextRadius(float ratio) {
        return getWidth() / 2 - (int) (getWidth() * ratio);
    }

    private void handleHapticFeedback(Azimuth azimuth) {
        if (lastHapticFeedbackPoint != null) {
            checkHapticFeedback(azimuth, lastHapticFeedbackPoint);
        } else {
            updateLastHapticFeedbackPoint(azimuth);
        }
    }

    private void checkHapticFeedback(Azimuth azimuth, Azimuth lastPoint) {
        Azimuth boundaryStart = lastPoint.minus(HAPTIC_FEEDBACK_INTERVAL);
        Azimuth boundaryEnd = lastPoint.plus(HAPTIC_FEEDBACK_INTERVAL);

        if (!MathUtils.isAzimuthBetweenTwoPoints(azimuth, boundaryStart, boundaryEnd)) {
            updateLastHapticFeedbackPoint(azimuth);
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    private void updateLastHapticFeedbackPoint(Azimuth azimuth) {
        float closestIntervalPoint = MathUtils.getClosestNumberFromInterval(azimuth.getDegrees(), HAPTIC_FEEDBACK_INTERVAL);
        lastHapticFeedbackPoint = new Azimuth(closestIntervalPoint);
    }
}
