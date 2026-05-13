package lk.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import lk.example.myapplication.database.AppDatabaseHelper;
import lk.example.myapplication.utils.ValidationManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private AppDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new AppDatabaseHelper(this);

        etUsername = findViewById(R.id.et_reset_username);
        etEmail = findViewById(R.id.et_reset_email);
        etNewPassword = findViewById(R.id.et_reset_password);
        etConfirmPassword = findViewById(R.id.et_reset_confirm_password);

        ImageView backButton = findViewById(R.id.btn_back_reset);
        Button btnResetPassword = findViewById(R.id.btn_reset_password);
        Button btnCancel = findViewById(R.id.btn_cancel_reset);

        backButton.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        String usernameError = ValidationManager.getUsernameError(username);
        if (usernameError != null) {
            Toast.makeText(this, usernameError, Toast.LENGTH_SHORT).show();
            return;
        }

        String emailError = ValidationManager.getEmailError(email);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordError = ValidationManager.getPasswordError(newPassword);
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationManager.passwordsMatch(newPassword, confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = dbHelper.getUserIdByUsernameAndEmail(username, email);
        if (userId == -1) {
            Toast.makeText(this, "No account matches that username and email", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUserPassword(userId, newPassword);
        if (result > 0) {
            Toast.makeText(this, "Password reset successful. Please sign in.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
        }
    }
}
