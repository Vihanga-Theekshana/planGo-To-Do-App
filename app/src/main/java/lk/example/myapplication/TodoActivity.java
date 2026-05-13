package lk.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import lk.example.myapplication.database.AppDatabaseHelper;
import lk.example.myapplication.utils.ValidationManager;

import java.util.Calendar;

public class TodoActivity extends AppCompatActivity {

    private AppDatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private LinearLayout taskListContainer;
    private String currentFilter = "All";
    private TextView tvGreeting, tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        dbHelper = new AppDatabaseHelper(this);
        sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            startActivity(new android.content.Intent(this, SignInActivity.class));
            finish();
            return;
        }

        taskListContainer = findViewById(R.id.task_list);
        tvGreeting = findViewById(R.id.greeting_text);
        tvSummary = findViewById(R.id.task_summary);

        String username = sessionManager.getUsername();
        tvGreeting.setText("Good Morning,\n" + username + "!");
        tvSummary.setText("You have 0 pending");

        BottomNavHelper.setup(this, BottomNavHelper.TAB_TASKS);

        setupFilterButtons();

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showAddTaskBottomSheet(false, -1));

        loadTasks();
    }

    private void setupFilterButtons() {
        Button all = findViewById(R.id.btn_filter_all);
        Button pending = findViewById(R.id.btn_filter_pending);
        Button completed = findViewById(R.id.btn_filter_completed);

        View.OnClickListener listener = v -> {
            all.setSelected(v == all);
            pending.setSelected(v == pending);
            completed.setSelected(v == completed);
            
            if (v == all) currentFilter = "All";
            else if (v == pending) currentFilter = "Pending";
            else if (v == completed) currentFilter = "Completed";
            
            loadTasks();
        };

        all.setOnClickListener(listener);
        pending.setOnClickListener(listener);
        completed.setOnClickListener(listener);

        all.setSelected(true);
    }

    private void loadTasks() {
        taskListContainer.removeAllViews();
        Cursor cursor = dbHelper.getTasks(sessionManager.getUserId(), currentFilter);
        
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_TIME));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_PRIORITY));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_STATUS));

                addTaskView(id, title, date, time, priority, status);
            } while (cursor.moveToNext());
        }
        cursor.close();

        int pendingCount = dbHelper.getPendingTaskCount(sessionManager.getUserId());
        tvSummary.setText("You have " + pendingCount + " pending");
    }

    private void addTaskView(int id, String title, String date, String time, String priority, int status) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.item_task_dynamic, taskListContainer, false);
        
        CheckBox cb = taskView.findViewById(R.id.cb_task_dynamic);
        TextView tvTitle = taskView.findViewById(R.id.tv_task_title_dynamic);
        TextView tvPriority = taskView.findViewById(R.id.tv_priority_dynamic);
        TextView tvDate = taskView.findViewById(R.id.tv_date_dynamic);
        ImageView ivDelete = taskView.findViewById(R.id.iv_delete_dynamic);
        ImageView ivEdit = taskView.findViewById(R.id.iv_edit_dynamic);

        tvTitle.setText(title);
        tvPriority.setText(priority);
        tvDate.setText(date);
        cb.setChecked(status == 1);

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.updateTaskStatus(id, isChecked ? 1 : 0);
            loadTasks();
        });

        ivDelete.setOnClickListener(v -> {
            dbHelper.deleteTask(id);
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            loadTasks();
        });

        ivEdit.setOnClickListener(v -> {
            showAddTaskBottomSheet(true, id);
        });

        taskListContainer.addView(taskView);
    }

    private void showAddTaskBottomSheet(boolean isEdit, int taskId) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.view_add_task_bottom_sheet, null);
        dialog.setContentView(sheetView);
        
        EditText etTitle = sheetView.findViewById(R.id.et_task_title_sheet);
        Button btnDate = sheetView.findViewById(R.id.btn_due_date_sheet);
        Button btnTime = sheetView.findViewById(R.id.btn_due_time_sheet);
        AutoCompleteTextView dropdownPriority = sheetView.findViewById(R.id.dropdown_priority_sheet);
        Button btnSubmit = sheetView.findViewById(R.id.btn_add_task_sheet);
        
        String[] priorities = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
        dropdownPriority.setAdapter(adapter);

        // If editing, load task data
        if (isEdit && taskId != -1) {
            Cursor taskCursor = dbHelper.getTaskById(taskId);
            if (taskCursor.moveToFirst()) {
                String title = taskCursor.getString(taskCursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_TITLE));
                String date = taskCursor.getString(taskCursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_DATE));
                String time = taskCursor.getString(taskCursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_TIME));
                String priority = taskCursor.getString(taskCursor.getColumnIndexOrThrow(AppDatabaseHelper.COL_TASK_PRIORITY));
                
                etTitle.setText(title);
                btnDate.setText(date);
                btnTime.setText(time);
                dropdownPriority.setText(priority, false);
            }
            taskCursor.close();
            btnSubmit.setText("Update Task");
        }

        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                btnDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                btnTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        sheetView.findViewById(R.id.btn_cancel_task_sheet).setOnClickListener(v -> dialog.dismiss());
        
        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String date = btnDate.getText().toString();
            String time = btnTime.getText().toString();
            String priority = dropdownPriority.getText().toString();

            // Validation
            if (!ValidationManager.isValidTaskTitle(title)) {
                Toast.makeText(this, "Please enter a valid task title (max 200 characters)", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (date.equals("Due Date")) {
                Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (time.equals("Due Time")) {
                Toast.makeText(this, "Please select a due time", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (priority.isEmpty()) {
                Toast.makeText(this, "Please select a priority", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit && taskId != -1) {
                // Update existing task
                int result = dbHelper.updateTask(taskId, title, date, time, priority);
                if (result > 0) {
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    loadTasks();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Add new task
                long result = dbHelper.addTask(sessionManager.getUserId(), title, date, time, priority);
                if (result != -1) {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    loadTasks();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}
