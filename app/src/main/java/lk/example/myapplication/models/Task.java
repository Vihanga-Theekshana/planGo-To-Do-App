package lk.example.myapplication.models;

public class Task {
    private int id;
    private int userId;
    private String title;
    private String dueDate;
    private String dueTime;
    private String priority;
    private int status; // 0 = Pending, 1 = Completed

    // Constructor with all fields
    public Task(int id, int userId, String title, String dueDate, String dueTime, String priority, int status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.status = status;
    }

    // Constructor without ID (for new tasks)
    public Task(int userId, String title, String dueDate, String dueTime, String priority) {
        this.userId = userId;
        this.title = title;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.status = 0; // Default to pending
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public String getPriority() {
        return priority;
    }

    public int getStatus() {
        return status;
    }

    public boolean isCompleted() {
        return status == 1;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", priority='" + priority + '\'' +
                ", status=" + status +
                '}';
    }
}
