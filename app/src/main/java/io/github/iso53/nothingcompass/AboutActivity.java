package io.github.iso53.nothingcompass;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import io.github.iso53.nothingcompass.preference.PreferenceConstants;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int themeMode = prefs.getInt(PreferenceConstants.THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.aboutToolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.aboutToolbar).setOnClickListener(v -> finish());
        ((androidx.appcompat.widget.Toolbar) findViewById(R.id.aboutToolbar))
                .setNavigationOnClickListener(v -> finish());

        TextView versionText = findViewById(R.id.textVersion);
        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            versionText.setText(getString(R.string.about_version, versionName));
        } catch (Exception e) {
            versionText.setText(getString(R.string.about_version, "1.0"));
        }
    }
}
