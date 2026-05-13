package lk.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import lk.example.myapplication.database.AppDatabaseHelper;

public class UserInfoActivity extends AppCompatActivity {

    private AppDatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TextView tvUsernameCard, tvEmailCard, tvDisplayName, tvProfileInitial, tvMemberSince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        dbHelper = new AppDatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        tvProfileInitial = findViewById(R.id.tv_profile_initial);
        tvDisplayName = findViewById(R.id.tv_display_name);
        tvUsernameCard = findViewById(R.id.tv_username_card);
        tvEmailCard = findViewById(R.id.tv_email_card);
        tvMemberSince = findViewById(R.id.tv_member_since);

        ImageView backButton = findViewById(R.id.btn_back_user);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoActivity.this, TodoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        Button btnEditInfo = findViewById(R.id.btn_edit_info);
        Button btnSignOut = findViewById(R.id.btn_sign_out);

        btnEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoActivity.this, EditInfoActivity.class);
            startActivity(intent);
        });

        btnSignOut.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(UserInfoActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavHelper.setup(this, BottomNavHelper.TAB_PROFILE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_NAME));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_EMAIL));
                    String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_CREATED_AT));

                    tvDisplayName.setText(username);
                    tvUsernameCard.setText(username);
                    tvEmailCard.setText(email);
                    if (tvMemberSince != null && createdAt != null) {
                        tvMemberSince.setText("Member since " + createdAt);
                    }

                    if (username.length() > 0) {
                        tvProfileInitial.setText(String.valueOf(username.charAt(0)).toUpperCase());
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }
}
