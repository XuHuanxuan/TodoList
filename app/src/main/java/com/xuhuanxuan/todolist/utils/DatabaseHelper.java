/** // 文件头注释：数据访问职责
 * 任务数据库访问层：创建/升级表结构，提供任务的增删改查与批量操作。
 */
package com.xuhuanxuan.todolist.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xuhuanxuan.todolist.dataclass.Task;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 数据库帮助类，用于管理任务数据的增删改查操作
 * 继承自SQLiteOpenHelper，提供对SQLite数据库的访问和操作
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1; // 版本号（影响 onUpgrade）
    private static final String TABLE_NAME = "tasks"; // 任务表名
    private static final String COLUMN_ID = "id"; // 主键
    private static final String COLUMN_TITLE = "title"; // 标题
    private static final String COLUMN_CONTENT = "content"; // 内容
    private static final String COLUMN_ATTACHMENT = "attachment"; // 附件
    private static final String COLUMN_DUE_DATE = "dueDate"; // 截止毫秒
    private static final String COLUMN_IMPORTANCE = "importance"; // 重要性
    private static final String COLUMN_CATEGORY = "category"; // 分类
    private static final String COLUMN_TAGS = "tags"; // 标签
    private static final String COLUMN_IS_COMPLETED = "isCompleted"; // 完成状态

    /**
     * 构造函数
     * @param context 上下文对象
     */
    public DatabaseHelper(Context context) { // 构造
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" + // DDL：创建任务表
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // 自增主键
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_CONTENT + " TEXT, " +
                        COLUMN_ATTACHMENT + " TEXT, " +
                        COLUMN_DUE_DATE + " INTEGER, " +
                        COLUMN_IMPORTANCE + " INTEGER, " +
                        COLUMN_CATEGORY + " TEXT, " +
                        COLUMN_TAGS + " TEXT, " +
                        COLUMN_IS_COMPLETED + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // 简单粗暴：丢弃旧表
        onCreate(db);
    }

    /**
     * 插入任务
     * @param task 要插入的任务对象
     * @return 新插入任务的行ID，如果插入失败则返回-1
     */
    public long insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_ATTACHMENT, task.getAttachment());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_IMPORTANCE, task.getImportance());
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_TAGS, String.join(",", task.getTags()));
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * 查询所有任务
     * @return 包含所有任务的列表
     */
    public List<Task> getAllTasks() { // 查询所有任务
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null); // 全表扫描

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                String attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT));
                long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
                int importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));

                String tagsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS));
                List<String> tags = new ArrayList<>();
                if (tagsString != null && !tagsString.isEmpty()) {
                    String[] tagArray = tagsString.split(",");
                    for (String tag : tagArray) {
                        tags.add(tag.trim());
                    }
                }

                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;

                Task task = new Task(
                        id,
                        title,
                        content,
                        attachment,
                        dueDate,
                        importance,
                        category,
                        tags,
                        isCompleted
                );
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    /**
     * 查询当天任务
     * @return 包含当天任务的列表
     */
    public List<Task> getTodayTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // 获取今天的日期字符串
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 查询当天的数据
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE strftime('%Y-%m-%d', " + COLUMN_DUE_DATE + " / 1000, 'unixepoch') = ?";
        Cursor cursor = db.rawQuery(query, new String[]{todayDate});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                String attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT));
                long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
                int importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));

                String tagsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS));
                List<String> tags = new ArrayList<>();
                if (tagsString != null && !tagsString.isEmpty()) {
                    String[] tagArray = tagsString.split(",");
                    for (String tag : tagArray) {
                        tags.add(tag.trim());
                    }
                }

                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;

                Task task = new Task(
                        id,
                        title,
                        content,
                        attachment,
                        dueDate,
                        importance,
                        category,
                        tags,
                        isCompleted
                );
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    /**
     * 根据id查询任务
     * @param taskId 任务ID
     * @return 对应的任务对象，如果未找到则返回null
     */
    public Task getTaskById(long taskId) {
        SQLiteDatabase db = getReadableDatabase();
        Task task = null;
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null,
                null,
                null
        );

        // 拿到任务数据了，直接创建对象返回，不用再循环游标了
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
            String attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT));
            long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
            int importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));

            String tagsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS));
            List<String> tags = new ArrayList<>();
            if (tagsString != null && !tagsString.isEmpty()) {
                String[] tagArray = tagsString.split(",");
                for (String tag : tagArray) {
                    tags.add(tag.trim());
                }
            }

            boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;

            task = new Task(
                    id,
                    title,
                    content,
                    attachment,
                    dueDate,
                    importance,
                    category,
                    tags,
                    isCompleted
            );
        }
        cursor.close();
        db.close();
        return task;
    }

    /**
     * 查询之前未完成的任务
     * @return 包含之前未完成任务的列表
     */
    public List<Task> getIncompleteTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // 获取今天的日期字符串
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 查询当天之前（不包括当天），未完成的任务数据
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                "strftime('%Y-%m-%d', " + COLUMN_DUE_DATE + " / 1000, 'unixepoch') < ? and " + COLUMN_IS_COMPLETED + " = 0";
        Cursor cursor = db.rawQuery(query, new String[]{todayDate});

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                String attachment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTACHMENT));
                long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
                int importance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMPORTANCE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));

                String tagsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAGS));
                List<String> tags = new ArrayList<>();
                if (tagsString != null && !tagsString.isEmpty()) {
                    String[] tagArray = tagsString.split(",");
                    for (String tag : tagArray) {
                        tags.add(tag.trim());
                    }
                }

                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;

                Task task = new Task(
                        id,
                        title,
                        content,
                        attachment,
                        dueDate,
                        importance,
                        category,
                        tags,
                        isCompleted
                );
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    /**
     * 更新任务
     * @param task 要更新的任务对象
     * @return 受影响的行数
     */
    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_ATTACHMENT, task.getAttachment());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_IMPORTANCE, task.getImportance());
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_TAGS, String.join(",", task.getTags()));
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
    }

    /**
     * 更新任务的截止日期
     * @param tasks 要更新的任务列表
     */
    public void updateTasksDueDate(List<Task> tasks) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayDate = calendar.getTimeInMillis();
        SQLiteDatabase db = this.getWritableDatabase();

        for (Task task : tasks) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DUE_DATE, todayDate);
            db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        }
        db.close();
    }

    /**
     * 将任务标记为已完成
     * @param tasks 要标记为已完成的任务列表
     */
    public void markTasksAsCompleted(List<Task> tasks) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (tasks.isEmpty()) {
            db.close();
            return;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_IS_COMPLETED + " = 1 WHERE " + COLUMN_ID + " IN (" + placeholders + ")";

        String[] params = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            params[i] = String.valueOf(tasks.get(i).getId());
        }

        db.execSQL(sql, params);
        db.close();
    }

    /**
     * 删除任务
     * @param taskId 要删除的任务ID
     * @return 受影响的行数
     */
    public int deleteTask(long taskId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
    }
}