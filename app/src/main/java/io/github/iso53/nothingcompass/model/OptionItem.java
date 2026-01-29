package io.github.iso53.nothingcompass.model;

import android.view.View;

public class OptionItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_OPTION = 1;

    public int type;
    public String title;
    public String subtitle;
    public int iconRes;
    public View.OnClickListener action;

    // Constructor for Header
    public OptionItem(String title) {
        this.type = TYPE_HEADER;
        this.title = title;
    }

    // Constructor for Option
    public OptionItem(String title, String subtitle, int iconRes, View.OnClickListener action) {
        this.type = TYPE_OPTION;
        this.title = title;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
        this.action = action;
    }
}
