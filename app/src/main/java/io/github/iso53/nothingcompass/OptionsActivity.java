package io.github.iso53.nothingcompass;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_options);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.optionsToolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.optionsToolbar).setOnClickListener(v -> finish());
        // Alternatively, use setNavigationOnClickListener if it's the back icon
        ((androidx.appcompat.widget.Toolbar) findViewById(R.id.optionsToolbar))
                .setNavigationOnClickListener(v -> finish());

        setupItems();
    }

    private void setupItems() {
        // App Category
        bindItem(R.id.itemSettings, R.drawable.ic_settings, R.string.item_settings, 0);
        bindItem(R.id.itemVersionUpdate, R.drawable.ic_version_update, R.string.item_version_update,
                R.string.item_version_update_sub);
        bindItem(R.id.itemAbout, R.drawable.ic_about, R.string.item_about, 0);
        bindItem(R.id.itemAuthor, R.drawable.ic_person, R.string.item_author, 0);
        bindItem(R.id.itemSourceCode, R.drawable.ic_code, R.string.item_source_code, 0);

        // Legal & Support
        bindItem(R.id.itemLicense, R.drawable.ic_license, R.string.item_license, 0);
        bindItem(R.id.itemThirdPartyLicenses, R.drawable.ic_verified, R.string.item_third_party_licenses, 0);
        bindItem(R.id.itemManagePermission, R.drawable.ic_permission, R.string.item_manage_permission, 0);
        bindItem(R.id.itemHelpFeedback, R.drawable.ic_help, R.string.item_help_feedback, 0);
        bindItem(R.id.itemRateApp, R.drawable.ic_rate, R.string.item_rate_app, 0);
    }

    private void bindItem(int viewId, int iconRes, int titleRes, int subtitleRes) {
        View view = findViewById(viewId);
        ((ImageView) view.findViewById(R.id.itemIcon)).setImageResource(iconRes);
        ((TextView) view.findViewById(R.id.itemTitle)).setText(titleRes);

        TextView subtitle = view.findViewById(R.id.itemSubtitle);
        if (subtitleRes != 0) {
            subtitle.setText(subtitleRes);
            subtitle.setVisibility(View.VISIBLE);
        } else {
            subtitle.setVisibility(View.GONE);
        }
    }
}
