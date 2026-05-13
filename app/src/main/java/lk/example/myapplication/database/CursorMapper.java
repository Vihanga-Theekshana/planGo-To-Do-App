package lk.example.myapplication.database;

import android.database.Cursor;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting database cursors to maps and objects
 */
public class CursorMapper {

    /**
     * Convert a cursor row to a HashMap
     */
    public static Map<String, String> cursorRowToMap(Cursor cursor) {
        Map<String, String> map = new HashMap<>();
        String[] columnNames = cursor.getColumnNames();
        for (String columnName : columnNames) {
            int index = cursor.getColumnIndex(columnName);
            if (index >= 0) {
                map.put(columnName, cursor.getString(index));
            }
        }
        return map;
    }

    /**
     * Get string value from cursor by column name
     */
    public static String getString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getString(index);
        }
        return null;
    }

    /**
     * Get int value from cursor by column name
     */
    public static int getInt(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getInt(index);
        }
        return 0;
    }

    /**
     * Get long value from cursor by column name
     */
    public static long getLong(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getLong(index);
        }
        return 0L;
    }

    /**
     * Get boolean value from cursor by column name (0 = false, non-zero = true)
     */
    public static boolean getBoolean(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getInt(index) != 0;
        }
        return false;
    }
}
