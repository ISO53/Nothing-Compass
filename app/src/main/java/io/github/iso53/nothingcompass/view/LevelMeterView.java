package io.github.iso53.nothingcompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;

public class LevelMeterView extends FrameLayout {

    // Constants
    private static final float LEVEL_THRESHOLD = 0.5f;
    private static final float ORIENTATION_THRESHOLD = 45f;
    private static final float RING_RADIUS_RATIO = 0.25f;
    private static final float LINE_LENGTH_RATIO = 0.75f;
    private static final float STROKE_WIDTH_DP = 1.5f;
    private static final int DEGREE_TEXT_SIZE_SP = 48;
    private static final float VIBRATION_DEGREE_INTERVAL = 1f; // Vibrate every N degrees
    private static final int VIBRATION_DURATION_MS = 10; // Short vibration

    // Colors
    private final int colorPrimary;
    private final int colorSecondary;
    private final int colorTertiary;

    // Paint objects
    private final Paint paint;
    private final Paint clearPaint;

    // UI components
    private final TextView degreeTextView;

    // State variables
    private float spin;
    private boolean isHorizontal;
    private float cx, cy;
    private float ringRadius;
    private int lastVibrationDegree = 0;
    private boolean isActive = false;
    private boolean isHapticFeedbackEnabled = true;

    public LevelMeterView(Context c, AttributeSet a) {
        super(c, a);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        setWillNotDraw(false); // FrameLayout doesn't draw by default

        // Initialize colors
        colorPrimary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary);
        colorSecondary = MaterialColors.getColor(this, R.attr.colorSecondary);
        colorTertiary = MaterialColors.getColor(this, R.attr.colorTertiary);

        // Initialize paint
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setColor(colorTertiary);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(dp(STROKE_WIDTH_DP));

        // Initialize clear paint for erasing inside the ring
        clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearPaint.setAntiAlias(true);
        clearPaint.setStyle(Paint.Style.FILL);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // Initialize degree display TextView
        degreeTextView = initializeDegreeTextView(c);
        addView(degreeTextView, createCenteredLayoutParams());

        // Initialize state
        isHorizontal = false;
        spin = 1f;
    }

    private TextView initializeDegreeTextView(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(colorSecondary);
        textView.setTextSize(DEGREE_TEXT_SIZE_SP);
        textView.setText(" 0°");
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(ResourcesCompat.getFont(context,
                io.github.iso53.nothingcompass.R.font.ndot57));
        return textView;
    }

    private FrameLayout.LayoutParams createCenteredLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cx = w * 0.5f;
        cy = h * 0.5f;
        ringRadius = Math.min(w, h) * RING_RADIUS_RATIO;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float lineLen = ringRadius * LINE_LENGTH_RATIO;

        drawOuterRing(canvas);
        drawReferenceLines(canvas, lineLen);
        drawRotatingLine(canvas, lineLen);
        eraseRingInterior(canvas);
    }

    private void drawOuterRing(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getRingColor());
        canvas.drawCircle(cx, cy, ringRadius, paint);
    }

    private void drawReferenceLines(Canvas canvas, float lineLen) {
        paint.setColor(getReferenceLineColor());

        if (isHorizontal) {
            // Draw vertical reference lines (top and bottom)
            canvas.drawLine(cx, cy - ringRadius, cx, cy - ringRadius - lineLen, paint);
            canvas.drawLine(cx, cy + ringRadius, cx, cy + ringRadius + lineLen, paint);
        } else {
            // Draw horizontal reference lines (left and right)
            canvas.drawLine(cx + ringRadius, cy, cx + ringRadius + lineLen, cy, paint);
            canvas.drawLine(cx - ringRadius, cy, cx - ringRadius - lineLen, cy, paint);
        }
    }

    private void drawRotatingLine(Canvas canvas, float lineLen) {
        canvas.save();
        paint.setColor(getRotatingLineColor());
        canvas.rotate(spin + 90f, cx, cy);
        canvas.drawLine(
                cx - ringRadius - lineLen,
                cy,
                cx + ringRadius + lineLen,
                cy,
                paint);
        canvas.restore();
    }

    private void eraseRingInterior(Canvas canvas) {
        canvas.drawCircle(cx, cy, ringRadius - paint.getStrokeWidth(), clearPaint);
    }

    /**
     * Updates device tilt using Android's gravity sensor coordinate system.
     *
     * <p>
     * Unlike a standard Cartesian plane, Android gravity values invert the X axis.
     * To properly resolve full 360° orientation, both {@code gx} and {@code gy}
     * must be
     * evaluated using a quadrant-based system.
     * </p>
     *
     * <pre>
     *              |
     *    Q2 (+,+)  |   Q1 (-,+)
     *              |
     * -------------+------------
     *              |
     *    Q3 (+,-)  |   Q4 (-,-)
     *              |
     * </pre>
     *
     * <p>
     * Quadrant sign mapping:
     * </p>
     * <ul>
     * <li>Q1 → {@code gx < 0 , gy > 0}</li>
     * <li>Q2 → {@code gx > 0 , gy > 0}</li>
     * <li>Q3 → {@code gx > 0 , gy < 0}</li>
     * <li>Q4 → {@code gx < 0 , gy < 0}</li>
     * </ul>
     *
     * <p>
     * Since the X axis is inverted relative to the mathematical coordinate system,
     * the spin angle is computed using {@code atan2(gy, -gx)}.
     * </p>
     *
     * @param gx gravity acceleration along the X axis (Android-inverted)
     * @param gy gravity acceleration along the Y axis
     */
    public void updateTilt(float gx, float gy) {
        calculateSpinAngle(gx, gy);
        updateOrientation();
        updateDegreeDisplay();
        handleHapticFeedback();
        invalidate();
    }

    private void handleHapticFeedback() {
        if (!isActive || !isHapticFeedbackEnabled)
            return;
        int currentDegreeInt = Math.round(spin / VIBRATION_DEGREE_INTERVAL);
        if (currentDegreeInt != lastVibrationDegree) {
            lastVibrationDegree = currentDegreeInt;
            performHapticFeedback();
        }
    }

    public void setIsActive(boolean active) {
        this.isActive = active;
    }

    public void setHapticFeedbackEnabled(boolean enabled) {
        this.isHapticFeedbackEnabled = enabled;
    }

    private void performHapticFeedback() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(
                    VIBRATION_DURATION_MS,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private void calculateSpinAngle(float gx, float gy) {
        // Convert to mathematical plane (invert X axis)
        float angleRad = (float) Math.atan2(gy, -gx);
        spin = (float) Math.toDegrees(angleRad);

        // Normalize to 0–360 range
        if (spin < 0) {
            spin += 360f;
        }
    }

    private void updateOrientation() {
        // Determine if the rotating line is closer to horizontal or vertical
        // The line orientation switches every 90 degrees
        float normalizedAngle = normalizeAngle(spin, 180f);
        isHorizontal =
                normalizedAngle < ORIENTATION_THRESHOLD || normalizedAngle > (180f - ORIENTATION_THRESHOLD);
    }

    private void updateDegreeDisplay() {
        int displayDegree = calculateDisplayDegree();
        degreeTextView.setTextColor(getDegreeTextColor());
        degreeTextView.setText(" " + displayDegree + "°");
        degreeTextView.setRotation(spin - 90f);
    }

    private int calculateDisplayDegree() {
        // Calculate angle relative to nearest 90-degree orientation (0, 90, 180, 270)
        float normalizedSpin = normalizeAngle(spin, 360f);
        float angleFromNearest90 = normalizedSpin % 90f;

        // Convert to -45 to +45 range (centered around each 90-degree mark)
        float degreeValue;
        if (angleFromNearest90 <= ORIENTATION_THRESHOLD) {
            degreeValue = angleFromNearest90; // 0 to 45
        } else {
            degreeValue = angleFromNearest90 - 90f; // 45 to 90 becomes -45 to 0
        }

        return Math.round(degreeValue);
    }

    // Color helper methods
    private int getRingColor() {
        return isNearLevel() ? colorPrimary : colorSecondary;
    }

    private int getReferenceLineColor() {
        return isNearLevel() ? colorPrimary : colorTertiary;
    }

    private int getRotatingLineColor() {
        return isNearLevel() ? colorPrimary : colorSecondary;
    }

    private int getDegreeTextColor() {
        return isNearLevel() ? colorPrimary : colorSecondary;
    }

    private boolean isNearLevel() {
        float mod = Math.abs(spin % 90f);
        return mod < LEVEL_THRESHOLD || mod > (90f - LEVEL_THRESHOLD);
    }

    private float normalizeAngle(float angle, float range) {
        return ((angle % range) + range) % range;
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
