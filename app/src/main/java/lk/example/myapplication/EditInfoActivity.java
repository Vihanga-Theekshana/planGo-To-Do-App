package lk.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import lk.example.myapplication.database.AppDatabaseHelper;
import lk.example.myapplication.utils.ValidationManager;

public class EditInfoActivity extends AppCompatActivity {

    private EditText etUsername, etEmail;
    private AppDatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String currentUsername = "";
    private String currentEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        dbHelper = new AppDatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        etUsername = findViewById(R.id.et_edit_username);
        etEmail = findViewById(R.id.et_edit_email);
        ImageView backButton = findViewById(R.id.btn_back_edit);
        Button btnSaveChanges = findViewById(R.id.btn_save_changes);
        Button btnCancel = findViewById(R.id.btn_cancel_edit);

        loadCurrentData();

        backButton.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validate username
        String usernameError = ValidationManager.getUsernameError(username);
        if (usernameError != null) {
            Toast.makeText(this, usernameError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email
        String emailError = ValidationManager.getEmailError(email);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        
        // Check if new username already exists (but allow current username)
        if (!username.equals(currentUsername) && dbHelper.usernameExists(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.equals(currentEmail) && dbHelper.emailExists(email)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUserInfo(userId, username, email);
        if (result > 0) {
            // Update session data with new username
            sessionManager.createLoginSession(userId, username);
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentData() {
        int userId = sessionManager.getUserId();
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    currentUsername = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_NAME));
                    currentEmail = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_EMAIL));
                    etUsername.setText(currentUsername);
                    etEmail.setText(currentEmail);
                }
            } finally {
                cursor.close();
            }
        }
    }
}
