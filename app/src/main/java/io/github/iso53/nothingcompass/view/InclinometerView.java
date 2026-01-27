package io.github.iso53.nothingcompass.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;

public class InclinometerView extends View {

    private int colorPrimary;
    private int colorSecondary;
    private final Paint paint;

    private float cx, cy;
    private float bubbleX, bubbleY;

    private float ringRadius;
    private float bubbleRadius;
    private boolean inCenter;

    private static final float SMOOTH = 0.75f;
    private static final float CENTER_THRESHOLD = 0.01f; // 1% of ring

    public InclinometerView(Context c, AttributeSet a) {
        super(c, a);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setColor(colorSecondary);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(dp(1.5f));

        colorPrimary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary);
        colorSecondary = MaterialColors.getColor(this, R.attr.colorSecondary);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cx = w * 0.5f;
        cy = h * 0.5f;

        ringRadius = Math.min(w, h) * 0.45f;
        bubbleRadius = ringRadius * 0.12f;

        bubbleX = cx;
        bubbleY = cy;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(inCenter ? colorPrimary : colorSecondary);

        // paint the outer ring
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cx, cy, ringRadius, paint);

        // paint lines
        float half = ringRadius * 0.5f;
        canvas.drawLine(cx, cy - half, cx, cy + half, paint);
        canvas.drawLine(cx - half, cy, cx + half, cy, paint);

        // paint the moving circle
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bubbleX, bubbleY, bubbleRadius, paint);
    }

    public void updateTilt(float pitch, float roll) {

        // Clamp so bubble never leaves circle
        float len = (float) Math.sqrt(roll * roll + pitch * pitch);
        if (len > 1f) {
            roll /= len;
            pitch /= len;
        }

        float targetX = cx + roll * ringRadius;
        float targetY = cy + pitch * ringRadius;

        // Proper smoothing toward target
        bubbleX += (targetX - bubbleX) * SMOOTH;
        bubbleY += (targetY - bubbleY) * SMOOTH;

        float distFromCenter = (float) Math.hypot(bubbleX - cx, bubbleY - cy);

        inCenter = distFromCenter < ringRadius * CENTER_THRESHOLD;

        invalidate();
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
