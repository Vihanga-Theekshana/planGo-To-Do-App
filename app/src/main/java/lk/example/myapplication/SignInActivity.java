package lk.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import lk.example.myapplication.database.AppDatabaseHelper;
import lk.example.myapplication.utils.ValidationManager;

public class SignInActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private AppDatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        dbHelper = new AppDatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(SignInActivity.this, TodoActivity.class));
            finish();
        }

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        TextView createAccount = findViewById(R.id.create_account);

        btnLogin.setOnClickListener(v -> loginUser());
        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class))
        );

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (!ValidationManager.isNotEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationManager.isNotEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check user credentials
        if (dbHelper.checkUser(username, password)) {
            Cursor cursor = dbHelper.getUserByUsername(username);
            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_USER_ID));
                sessionManager.createLoginSession(userId, username);

                Intent intent = new Intent(SignInActivity.this, TodoActivity.class);
                startActivity(intent);
                finish();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

}
