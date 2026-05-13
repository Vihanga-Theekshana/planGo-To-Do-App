package lk.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;

public final class BottomNavHelper {

    public static final int TAB_TASKS = 0;
    public static final int TAB_PROFILE = 1;
    public static final int TAB_DEV_INFO = 2;

    private BottomNavHelper() {
    }

    public static void setup(Activity activity, int selectedTab) {
        View tasks = activity.findViewById(R.id.nav_item_tasks);
        View profile = activity.findViewById(R.id.nav_item_profile);
        View devInfo = activity.findViewById(R.id.nav_item_dev_info);

        if (tasks == null || profile == null || devInfo == null) {
            return;
        }

        tasks.setSelected(selectedTab == TAB_TASKS);
        profile.setSelected(selectedTab == TAB_PROFILE);
        devInfo.setSelected(selectedTab == TAB_DEV_INFO);

        tasks.setOnClickListener(v -> {
            if (selectedTab != TAB_TASKS) {
                activity.startActivity(new Intent(activity, TodoActivity.class));
                applyNoAnimationTransition(activity);
                activity.finish();
            }
        });

        profile.setOnClickListener(v -> {
            if (selectedTab != TAB_PROFILE) {
                activity.startActivity(new Intent(activity, UserInfoActivity.class));
                applyNoAnimationTransition(activity);
                activity.finish();
            }
        });

        devInfo.setOnClickListener(v -> {
            if (selectedTab != TAB_DEV_INFO) {
                activity.startActivity(new Intent(activity, DeveloperInfoActivity.class));
                applyNoAnimationTransition(activity);
                activity.finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private static void applyNoAnimationTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    0,
                    0
            );
        } else {
            activity.overridePendingTransition(0, 0);
        }
    }
}
