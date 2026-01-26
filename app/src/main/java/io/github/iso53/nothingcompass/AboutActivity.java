package io.github.iso53.nothingcompass;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
