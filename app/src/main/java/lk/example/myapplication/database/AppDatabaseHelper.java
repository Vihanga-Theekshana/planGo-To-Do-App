package lk.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.example.myapplication.models.Task;
import lk.example.myapplication.utils.SecurityManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PlanGo.db";
    private static final int DATABASE_VERSION = 3;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_CREATED_AT = "created_at";

    // Tasks table
    public static final String TABLE_TASKS = "tasks";
    public static final String COL_TASK_ID = "id";
    public static final String COL_TASK_USER_ID = "user_id";
    public static final String COL_TASK_TITLE = "title";
    public static final String COL_TASK_DATE = "due_date";
    public static final String COL_TASK_TIME = "due_time";
    public static final String COL_TASK_PRIORITY = "priority";
    public static final String COL_TASK_STATUS = "status";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT UNIQUE NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_CREATED_AT + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_USER_ID + " INTEGER NOT NULL, " +
                COL_TASK_TITLE + " TEXT NOT NULL, " +
                COL_TASK_DATE + " TEXT NOT NULL, " +
                COL_TASK_TIME + " TEXT NOT NULL, " +
                COL_TASK_PRIORITY + " TEXT NOT NULL, " +
                COL_TASK_STATUS + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createTasksTable);

        // Create indexes for better query performance
        db.execSQL("CREATE INDEX idx_user_tasks ON " + TABLE_TASKS + "(" + COL_TASK_USER_ID + ")");
        db.execSQL("CREATE INDEX idx_task_status ON " + TABLE_TASKS + "(" + COL_TASK_STATUS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_USER_CREATED_AT + " TEXT");
        }
        if (oldVersion < 3) {
            // Add constraints and indexes for v3
            try {
                db.execSQL("CREATE INDEX idx_user_tasks ON " + TABLE_TASKS + "(" + COL_TASK_USER_ID + ")");
                db.execSQL("CREATE INDEX idx_task_status ON " + TABLE_TASKS + "(" + COL_TASK_STATUS + ")");
            } catch (Exception e) {
                // Indexes may already exist
            }
        }
    }

    // ============ USER OPERATIONS ============

    /**
     * Register a new user with password hashing
     */
    public long registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, username);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, SecurityManager.hashPassword(password));

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        values.put(COL_USER_CREATED_AT, sdf.format(new Date()));

        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Check if user exists and password is correct
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_NAME + " = ?";
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_PASSWORD}, selection, 
                new String[]{username}, null, null, null);
        
        boolean exists = false;
        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(0);
            exists = SecurityManager.verifyPassword(password, storedHash);
        }
        cursor.close();
        return exists;
    }

    /**
     * Get user by username
     */
    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_NAME + " = ?", new String[]{username}, 
                null, null, null);
    }

    /**
     * Get user by ID
     */
    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_ID + " = ?", new String[]{String.valueOf(id)}, 
                null, null, null);
    }

    /**
     * Get user ID by matching username and email
     */
    public int getUserIdByUsernameAndEmail(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_NAME + " = ? AND " + COL_USER_EMAIL + " = ?",
                new String[]{username, email},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
            }
            return -1;
        } finally {
            cursor.close();
        }
    }

    /**
     * Check if username already exists
     */
    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID}, COL_USER_NAME + " = ?", 
                new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID}, COL_USER_EMAIL + " = ?", 
                new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Update user profile (username and email)
     */
    public int updateUserInfo(int userId, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, username);
        values.put(COL_USER_EMAIL, email);
        return db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    /**
     * Update user password
     */
    public int updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, SecurityManager.hashPassword(newPassword));
        return db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    // ============ TASK OPERATIONS ============

    /**
     * Add a new task
     */
    public long addTask(int userId, String title, String date, String time, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_USER_ID, userId);
        values.put(COL_TASK_TITLE, title);
        values.put(COL_TASK_DATE, date);
        values.put(COL_TASK_TIME, time);
        values.put(COL_TASK_PRIORITY, priority);
        values.put(COL_TASK_STATUS, 0); // Default to pending
        return db.insert(TABLE_TASKS, null, values);
    }

    /**
     * Get all tasks for a user with filter
     */
    public Cursor getTasks(int userId, String filter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_TASK_USER_ID + " = ?";
        String[] args;
        
        if (filter.equals("All")) {
            args = new String[]{String.valueOf(userId)};
        } else {
            selection += " AND " + COL_TASK_STATUS + " = ?";
            args = new String[]{String.valueOf(userId), filter.equals("Completed") ? "1" : "0"};
        }
        
        return db.query(TABLE_TASKS, null, selection, args, null, null, COL_TASK_ID + " DESC");
    }

    /**
     * Get task by ID
     */
    public Cursor getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TASKS, null, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)}, 
                null, null, null);
    }

    /**
     * Get all tasks as Task objects (more type-safe)
     */
    public List<Task> getAllTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COL_TASK_USER_ID + " = ?", 
                new String[]{String.valueOf(userId)}, null, null, COL_TASK_ID + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_PRIORITY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_STATUS))
                );
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    /**
     * Update task status (complete/incomplete)
     */
    public int updateTaskStatus(int taskId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_STATUS, status);
        return db.update(TABLE_TASKS, values, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    /**
     * Update complete task details
     */
    public int updateTask(int taskId, String title, String date, String time, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, title);
        values.put(COL_TASK_DATE, date);
        values.put(COL_TASK_TIME, time);
        values.put(COL_TASK_PRIORITY, priority);
        return db.update(TABLE_TASKS, values, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    /**
     * Delete a task
     */
    public int deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TASKS, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    /**
     * Get count of pending tasks
     */
    public int getPendingTaskCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{"COUNT(*)"}, 
                COL_TASK_USER_ID + " = ? AND " + COL_TASK_STATUS + " = ?", 
                new String[]{String.valueOf(userId), "0"}, null, null, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Get count of completed tasks
     */
    public int getCompletedTaskCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{"COUNT(*)"}, 
                COL_TASK_USER_ID + " = ? AND " + COL_TASK_STATUS + " = ?", 
                new String[]{String.valueOf(userId), "1"}, null, null, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Delete all tasks for a user (cascade on user deletion)
     */
    public void deleteUserTasks(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COL_TASK_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }
}
