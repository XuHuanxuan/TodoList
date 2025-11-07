/** // 文件头注释：Activity 作用
 * 编辑任务页：加载既有任务，允许用户修改并保存更新。
 */
package com.xuhuanxuan.todolist.tab1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xuhuanxuan.todolist.R;
import com.xuhuanxuan.todolist.dataclass.Task;
import com.xuhuanxuan.todolist.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText titleEditText;
    private EditText contentEditText;
    private EditText attachmentEditText;
    private Button dueDateButton;
    private Spinner importanceSpinner;
    private Spinner spinnerCategory;
    private EditText tagsEditText;
    private CheckBox isCompletedCheckbox;
    private Button saveTaskButton;

    private long taskId = -1; // 用于存储任务的 ID
    private long dueDate = System.currentTimeMillis(); // 初始截止日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        dbHelper = new DatabaseHelper(this); // 初始化数据库

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        attachmentEditText = findViewById(R.id.attachmentEditText);
        dueDateButton = findViewById(R.id.dueDateButton);
        importanceSpinner = findViewById(R.id.importanceSpinner);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tagsEditText = findViewById(R.id.tagsEditText);
        isCompletedCheckbox = findViewById(R.id.isCompletedCheckbox);
        saveTaskButton = findViewById(R.id.saveTaskButton);

        // 获取传入的任务 ID
        taskId = getIntent().getLongExtra("TASK_ID", -1);

        // 日期选择器
        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        // 修改任务
        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskChanges();
            }
        });
    }

    private void loadTaskData() { // 加载任务数据到表单
        Task task = dbHelper.getTaskById(taskId);
        if (task != null) {
            titleEditText.setText(task.getTitle());
            contentEditText.setText(task.getContent());
            attachmentEditText.setText(task.getAttachment() != null ? task.getAttachment() : "无");
            dueDate = task.getDueDate();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dueDateButton.setText(dateFormat.format(new Date(dueDate)));

            importanceSpinner.setSelection(task.getImportance() - 1); // 假设重要性从1开始

            String[] categories = getResources().getStringArray(R.array.category_options);
            String taskCategory = task.getCategory();
            int categoryIndex = -1; // 默认未找到
            for (int i = 0; i < categories.length; i++) { // 顺序查找匹配索引
                if (categories[i].equals(taskCategory)) {
                    categoryIndex = i;
                    break;
                }
            }

            if (categoryIndex >= 0) {
                spinnerCategory.setSelection(categoryIndex);
            } else {
                spinnerCategory.setSelection(0);
            }

            // 将标签列表转为逗号分隔的字符串
            List<String> tags = task.getTags();
            StringBuilder tagsBuilder = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                if (i > 0) {
                    tagsBuilder.append(", ");
                }
                tagsBuilder.append(tags.get(i));
            }
            tagsEditText.setText(tagsBuilder.toString());

            isCompletedCheckbox.setChecked(task.isCompleted());
        } else {
            Toast.makeText(this, "未找到任务信息", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDatePicker() { // 弹出日期选择器
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueDate);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        dueDate = calendar.getTimeInMillis();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // 格式化
                        dueDateButton.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void saveTaskChanges() {
        String newTitle = titleEditText.getText().toString();
        String newContent = contentEditText.getText().toString();
        String newAttachment = attachmentEditText.getText().toString();
        int newImportance = importanceSpinner.getSelectedItemPosition() + 1;
        String newCategory = spinnerCategory.getSelectedItem().toString();

        // 处理标签
        String tagsString = tagsEditText.getText().toString();
        List<String> newTags = new ArrayList<>();
        if (!tagsString.isEmpty()) {
            String[] tagArray = tagsString.split(",");
            for (String tag : tagArray) {
                newTags.add(tag.trim());
            }
        }

        boolean isCompleted = isCompletedCheckbox.isChecked();

        if (!newTitle.isEmpty() && !newContent.isEmpty()) { // 表单校验
            // 更新任务数据
            Task updatedTask = new Task(
                    taskId, // ID
                    newTitle, // 标题
                    newContent, // 内容
                    newAttachment, // 附件
                    dueDate, // 截止
                    newImportance, // 重要性
                    newCategory, // 分类
                    newTags, // 标签
                    isCompleted // 完成
            );

            int result = dbHelper.updateTask(updatedTask);
            if (result > 0) {
                Intent intent = new Intent();
                intent.putExtra("UPDATED_TASK_ID", taskId);
                setResult(RESULT_OK, intent);
                Toast.makeText(this, "任务修改成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "修改任务失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请填写完整的任务信息", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskData();
    }
}