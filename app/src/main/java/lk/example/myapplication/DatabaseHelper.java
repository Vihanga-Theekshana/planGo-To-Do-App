package lk.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PlanGo.db";
    private static final int DATABASE_VERSION = 2;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_CREATED_AT + " TEXT)";
        db.execSQL(createUsersTable);

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_USER_ID + " INTEGER, " +
                COL_TASK_TITLE + " TEXT, " +
                COL_TASK_DATE + " TEXT, " +
                COL_TASK_TIME + " TEXT, " +
                COL_TASK_PRIORITY + " TEXT, " +
                COL_TASK_STATUS + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_USER_CREATED_AT + " TEXT");
        }
    }

    public long registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, username);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        values.put(COL_USER_CREATED_AT, sdf.format(new Date()));
        
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_NAME + " = ? AND " + COL_USER_PASSWORD + " = ?";
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID}, selection, new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_NAME + " = ?", new String[]{username}, null, null, null);
    }

    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public int updateUserInfo(int userId, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, username);
        values.put(COL_USER_EMAIL, email);
        return db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public long addTask(int userId, String title, String date, String time, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_USER_ID, userId);
        values.put(COL_TASK_TITLE, title);
        values.put(COL_TASK_DATE, date);
        values.put(COL_TASK_TIME, time);
        values.put(COL_TASK_PRIORITY, priority);
        return db.insert(TABLE_TASKS, null, values);
    }

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

    public void updateTaskStatus(int taskId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_STATUS, status);
        db.update(TABLE_TASKS, values, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public void updateTask(int taskId, String title, String date, String time, String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, title);
        values.put(COL_TASK_DATE, date);
        values.put(COL_TASK_TIME, time);
        values.put(COL_TASK_PRIORITY, priority);
        db.update(TABLE_TASKS, values, COL_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }
}
