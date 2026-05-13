package lk.example.myapplication;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

import lk.example.myapplication.database.AppDatabaseHelper;
import lk.example.myapplication.utils.ValidationManager;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private AppDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new AppDatabaseHelper(this);

        etUsername = findViewById(R.id.et_signup_username);
        etEmail = findViewById(R.id.et_signup_email);
        etPassword = findViewById(R.id.et_signup_password);
        etConfirmPassword = findViewById(R.id.et_signup_confirm_password);
        MaterialButton btnSignUp = findViewById(R.id.btn_signup);
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView loginLink = findViewById(R.id.login_link);

        styleLoginLink(loginLink);

        btnBack.setOnClickListener(v -> finish());
        loginLink.setOnClickListener(v -> finish());

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate username
        String usernameError = ValidationManager.getUsernameError(username);
        if (usernameError != null) {
            Toast.makeText(this, usernameError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username exists
        if (dbHelper.usernameExists(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email
        String emailError = ValidationManager.getEmailError(email);
        if (emailError != null) {
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email exists
        if (dbHelper.emailExists(email)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password
        String passwordError = ValidationManager.getPasswordError(password);
        if (passwordError != null) {
            Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check password confirmation
        if (!ValidationManager.passwordsMatch(password, confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // All validations passed, register user
        long result = dbHelper.registerUser(username, email, password);
        if (result != -1) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void styleLoginLink(TextView textView) {
        String value = getString(R.string.already_have_account);
        SpannableString spannable = new SpannableString(value);
        int start = value.indexOf("Login");
        if (start >= 0) {
            int end = start + "Login".length();
            spannable.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        textView.setText(spannable);
    }
}
