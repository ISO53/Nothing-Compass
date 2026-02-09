package io.github.iso53.nothingcompass;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.Objects;

import io.github.iso53.nothingcompass.fragment.CompassFragment;
import io.github.iso53.nothingcompass.fragment.InclinometerFragment;
import io.github.iso53.nothingcompass.preference.PreferenceConstants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int themeMode = prefs.getInt(PreferenceConstants.THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.mainToolbar));

        // Remove app name from toolbar
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ViewPager2 viewPager2 = findViewById(R.id.mainViewPager2);
        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new CompassFragment();
                    case 1:
                        return new InclinometerFragment();
                    default:
                        throw new IllegalStateException("Invalid position.");
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        TabLayout tabLayout = findViewById(R.id.mainTabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            // No text needed for dot indicator
        }).attach();

        checkAndRequestReview();
    }

    private void checkAndRequestReview() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasAsked = prefs.getBoolean(PreferenceConstants.HAS_ASKED_FOR_REVIEW, false);
        if (hasAsked)
            return;

        int launchCount = prefs.getInt(PreferenceConstants.APP_LAUNCH_COUNT, 0) + 1;
        prefs.edit().putInt(PreferenceConstants.APP_LAUNCH_COUNT, launchCount).apply();

        // Let's ask after 5 launches
        if (launchCount >= 5) {
            requestInAppReview(prefs);
        }
    }

    private void requestInAppReview(SharedPreferences prefs) {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            ReviewInfo reviewInfo = task.getResult();
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(reviewFlowTask -> prefs.edit()
                    .putBoolean(PreferenceConstants.HAS_ASKED_FOR_REVIEW, true).apply());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_options) {
            android.content.Intent intent = new android.content.Intent(this, OptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}