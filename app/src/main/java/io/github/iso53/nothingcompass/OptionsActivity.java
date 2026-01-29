package io.github.iso53.nothingcompass;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.iso53.nothingcompass.model.OptionItem;
import io.github.iso53.nothingcompass.view.OptionsAdapter;

public class OptionsActivity extends AppCompatActivity {

    private static void onClick(View v) {
        // TODO: Show OSS licenses
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_options);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.optionsToolbar),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                    return insets;
                });

        // Handle bottom padding for RecyclerView to avoid navigation bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.optionsRecyclerView), (v,
                                                                                           insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(),
                    systemBars.bottom + v.getPaddingBottom());
            return insets;
        });

        // Setup Toolbar
        findViewById(R.id.optionsToolbar).setOnClickListener(v -> finish());
        ((androidx.appcompat.widget.Toolbar) findViewById(R.id.optionsToolbar))
                .setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.optionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<OptionItem> items = new ArrayList<>();

        // Category: App
        items.add(new OptionItem(getString(R.string.category_app)));
        items.add(new OptionItem(getString(R.string.item_settings), null, R.drawable.ic_settings,
                v -> {
                    // TODO: Navigate to settings
                }));
        items.add(new OptionItem(getString(R.string.item_version_update), null,
                R.drawable.ic_version_update, v -> openPlayStore()));
        items.add(new OptionItem(getString(R.string.item_about), null, R.drawable.ic_about,
                v -> startActivity(new Intent(this, AboutActivity.class))));
        items.add(new OptionItem(getString(R.string.item_author), null, R.drawable.ic_person,
                v -> openUrl("https://github.com/iso53")));
        items.add(new OptionItem(getString(R.string.item_source_code), null, R.drawable.ic_code,
                v -> openUrl("https://github.com/iso53/Nothing-Compass")));

        // Category: Support
        items.add(new OptionItem(getString(R.string.category_support)));
        items.add(new OptionItem(getString(R.string.item_license), null, R.drawable.ic_license,
                v -> openUrl("https://github.com/iso53/Nothing-Compass/blob/main/LICENSE.md")));
        items.add(new OptionItem(getString(R.string.item_third_party_licenses), null,
                R.drawable.ic_verified, OptionsActivity::onClick));
        items.add(new OptionItem(getString(R.string.item_manage_permission), null,
                R.drawable.ic_permission, v -> openAppSettings()));
        items.add(new OptionItem(getString(R.string.item_help_feedback), null, R.drawable.ic_help,
                v -> sendFeedbackEmail()));
        items.add(new OptionItem(getString(R.string.item_rate_app), null, R.drawable.ic_rate,
                v -> openPlayStore()));

        OptionsAdapter adapter = new OptionsAdapter(items);
        recyclerView.setAdapter(adapter);
    }

    private void openPlayStore() {
        String packageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void sendFeedbackEmail() {
        String feedbackEmail = "ihsansimsek5335@gmail.com";
        String appVersion = "Unknown";
        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {
        }

        String deviceInfo = "\n\n\n------------------------------" +
                "\nDevice Diagnostics (Please do not delete):" +
                "\nApp Version: " + appVersion +
                "\nAndroid Version: " + android.os.Build.VERSION.RELEASE + " (SDK "
                + android.os.Build.VERSION.SDK_INT + ")" +
                "\nManufacturer: " + android.os.Build.MANUFACTURER +
                "\nModel: " + android.os.Build.MODEL +
                "\nProduct: " + android.os.Build.PRODUCT;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{feedbackEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Support - Nothing Compass");
        intent.putExtra(Intent.EXTRA_TEXT, deviceInfo);

        try {
            startActivity(Intent.createChooser(intent, "Send Feedback"));
        } catch (ActivityNotFoundException e) {
            // Fallback to GitHub issues if no email app found
            openUrl("https://github.com/iso53/Nothing-Compass/issues");
        }
    }
}
