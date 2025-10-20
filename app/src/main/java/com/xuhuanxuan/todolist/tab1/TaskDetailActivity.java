/** // 文件头注释：Activity 作用
 * 任务详情页：展示任务完整信息，支持编辑与删除。
 */
package com.xuhuanxuan.todolist.tab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.xuhuanxuan.todolist.MainActivity;
import com.xuhuanxuan.todolist.R;
import com.xuhuanxuan.todolist.dataclass.Task;
import com.xuhuanxuan.todolist.utils.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<Intent> editTaskLauncher;
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView attachmentTextView;
    private TextView dueDateTextView;
    private TextView importanceTextView;
    private TextView categoryTextView;
    private TextView tagsTextView;
    private TextView isCompletedTextView;

    private Button editTaskButton;
    private Button deleteTaskButton;

    private long taskId = -1;

    @Override // 生命周期：创建阶段
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Button btnReturn = findViewById(R.id.btnReturn);
        // 设置"返回"按钮点击事件
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dbHelper = new DatabaseHelper(this);

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        attachmentTextView = findViewById(R.id.attachmentTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        importanceTextView = findViewById(R.id.importanceTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        isCompletedTextView = findViewById(R.id.isCompletedTextView);

        editTaskButton = findViewById(R.id.editTaskButton);
        deleteTaskButton = findViewById(R.id.deleteTaskButton);

        // 与编辑页绑定契约，更新参数
        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> { // 回调
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // 更新任务的标题、内容和其他字段
                            String updatedTitle = data.getStringExtra("UPDATED_TASK_TITLE");
                            if (updatedTitle != null) titleTextView.setText(updatedTitle);

                            String updatedContent = data.getStringExtra("UPDATED_TASK_CONTENT");
                            if (updatedContent != null) contentTextView.setText(updatedContent);

                            String updatedAttachment = data.getStringExtra("UPDATED_TASK_ATTACHMENT");
                            if (updatedAttachment != null) attachmentTextView.setText(updatedAttachment);

                            String updatedDueDate = data.getStringExtra("UPDATED_TASK_DUE_DATE");
                            if (updatedDueDate != null) dueDateTextView.setText(updatedDueDate);

                            String updatedImportance = data.getStringExtra("UPDATED_TASK_IMPORTANCE");
                            if (updatedImportance != null) importanceTextView.setText(updatedImportance);

                            String updatedCategory = data.getStringExtra("UPDATED_TASK_CATEGORY");
                            if (updatedCategory != null) categoryTextView.setText(updatedCategory);

                            String updatedTags = data.getStringExtra("UPDATED_TASK_TAGS");
                            if (updatedTags != null) tagsTextView.setText(updatedTags);

                            boolean isCompleted = data.getBooleanExtra("UPDATED_TASK_IS_COMPLETED", false);
                            isCompletedTextView.setText(isCompleted ? "已完成" : "未完成");
                        }
                    }
                }
        );

        // 获取传入的任务 ID 和信息
        taskId = getIntent().getLongExtra("TASK_ID", -1);
        loadTaskData();

        // 修改任务，携带参数，跳转到EditTaskActivity
        editTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskDetailActivity.this, EditTaskActivity.class);
                intent.putExtra("TASK_ID", taskId);
                editTaskLauncher.launch(intent);
            }
        });

        // 删除任务，删除成功后跳转回首页
        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });
    }

    private void deleteTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除任务")
                .setMessage("确定删除这个任务吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    int result = dbHelper.deleteTask(taskId);
                    if (result > 0) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    Toast.makeText(this, "取消删除", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void loadTaskData() {
        Task task = dbHelper.getTaskById(taskId);
        if (task != null) {
            titleTextView.setText(task.getTitle());
            contentTextView.setText(task.getContent());
            attachmentTextView.setText(task.getAttachment() != null ? task.getAttachment() : "无");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dueDateTextView.setText(dateFormat.format(new Date(task.getDueDate())));

            String importanceText;
            switch (task.getImportance()) {
                case 1:
                    importanceText = "小事";
                    break;
                case 2:
                    importanceText = "中等";
                    break;
                case 3:
                    importanceText = "重要";
                    break;
                default:
                    importanceText = "小事";
            }
            importanceTextView.setText(importanceText);

            categoryTextView.setText(task.getCategory() != null ? task.getCategory() : "无");

            // 将列表转换为字符串显示
            StringBuilder tagsBuilder = new StringBuilder();
            for (String tag : task.getTags()) {
                if (tagsBuilder.length() > 0) {
                    tagsBuilder.append(", ");
                }
                tagsBuilder.append(tag);
            }
            tagsTextView.setText(tagsBuilder.length() > 0 ? tagsBuilder.toString() : "无标签");

            isCompletedTextView.setText(task.isCompleted() ? "已完成" : "未完成");
        } else {
            Toast.makeText(this, "未找到任务", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskData();
    }
}