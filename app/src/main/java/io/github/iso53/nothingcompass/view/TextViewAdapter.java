package io.github.iso53.nothingcompass.view;

import androidx.databinding.BindingAdapter;

public final class TextViewAdapter {
    private TextViewAdapter() {}

    @BindingAdapter("azimuth")
    public static void setAzimuth(CompassView view, float value) {
        view.setAzimuth(value);
    }

    @BindingAdapter("android:compoundDrawableTint")
    public static void setCompoundDrawableTint(android.widget.TextView textView, int colorAttributeResourceId) {
        int color = com.google.android.material.color.MaterialColors.getColor(textView, colorAttributeResourceId);
        android.content.res.ColorStateList colorStateList = android.content.res.ColorStateList.valueOf(color);
        androidx.core.widget.TextViewCompat.setCompoundDrawableTintList(textView, colorStateList);
    }
}
