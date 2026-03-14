package io.github.iso53.nothingcompass;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import io.github.iso53.nothingcompass.preference.PreferenceConstants;

public class NorthTypeActivity extends AppCompatActivity {

    private ImageView northAnimation;
    private MaterialSwitch switchTrueNorth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_north_type);

        setupToolbar();

        northAnimation = findViewById(R.id.north_animation);
        switchTrueNorth = findViewById(R.id.switch_true_north);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isTrueNorth = prefs.getBoolean(PreferenceConstants.TRUE_NORTH, false);
        switchTrueNorth.setChecked(isTrueNorth);

        switchTrueNorth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PreferenceConstants.TRUE_NORTH, isChecked).apply();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Apply custom font to CollapsingToolbarLayout
        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapseToolbar);
        android.graphics.Typeface typeface = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.ntype82headline);
        if (typeface != null) {
            collapsingToolbar.setExpandedTitleTypeface(typeface);
            collapsingToolbar.setCollapsedTitleTypeface(typeface);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (northAnimation.getDrawable() instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) northAnimation.getDrawable()).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (northAnimation.getDrawable() instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) northAnimation.getDrawable()).stop();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
