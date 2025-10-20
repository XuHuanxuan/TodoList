/** // 文件头注释：Activity 作用
 * 新增任务页：填写任务信息并保存到本地 SQLite。
 */
package com.xuhuanxuan.todolist.tab1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText titleEditText;
    private EditText contentEditText;
    private EditText attachment;
    private Button dueDateButton;
    private Spinner importanceSpinner;
    private Spinner categorySpinner;
    private EditText tagsEditText;
    private CheckBox isCompletedCheckbox;
    private Button submitTaskButton;

    private Long dueDate = null; // 将 dueDate 初始设为 null，表示还未选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = new DatabaseHelper(this); // 初始化数据库

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        attachment = findViewById(R.id.attachment);
        dueDateButton = findViewById(R.id.dueDateButton);
        importanceSpinner = findViewById(R.id.spinnerImportance);
        categorySpinner = findViewById(R.id.spinnerCategory);
        tagsEditText = findViewById(R.id.etTags);
        isCompletedCheckbox = findViewById(R.id.checkIsCompleted);
        submitTaskButton = findViewById(R.id.submitTaskButton);

        // 设置按钮点击事件，弹出日期选择对话框
        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(); // 当前时间
                DatePickerDialog datePicker = new DatePickerDialog(
                        AddTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() { // 日期回调
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) { // 选择日期
                                calendar.set(year, month, dayOfMonth); // 设置年月日
                                calendar.set(Calendar.HOUR_OF_DAY, 8);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                dueDate = calendar.getTimeInMillis();
                                // 将选择的日期显示在按钮上
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // 格式化
                                dueDateButton.setText(dateFormat.format(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePicker.show(); // 显示选择器
            }
        });

        submitTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 点击响应
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                String attachmentText = attachment.getText().toString();
                int importance = importanceSpinner.getSelectedItemPosition() + 1; // 假设 1 表示最低重要性
                String category = categorySpinner.getSelectedItem().toString();

                // 将标签以逗号分隔存储为列表
                String tagsString = tagsEditText.getText().toString();
                List<String> tags = new ArrayList<>();
                if (!tagsString.isEmpty()) { // 非空解析
                    String[] tagArray = tagsString.split(",");
                    for (String tag : tagArray) {
                        tags.add(tag.trim());
                    }
                }

                boolean isCompleted = isCompletedCheckbox.isChecked();

                // 检查是否选择了截止日期
                if (!title.isEmpty() && !content.isEmpty() && dueDate != null) {
                    // 创建新的任务对象
                    Task newTask = new Task(
                            0L, // 临时 ID（自增）
                            title, // 标题
                            content, // 内容
                            attachmentText, // 附件
                            dueDate, // 截止
                            importance, // 重要性
                            category, // 分类
                            tags, // 标签
                            isCompleted // 完成
                    );

                    // 插入任务到数据库
                    long result = db.insertTask(newTask);
                    if (result != -1L) {
                        Toast.makeText(AddTaskActivity.this, "任务添加成功", Toast.LENGTH_SHORT).show();
                        Log.d("AddTaskActivity", "任务已添加到数据库");
                        finish();
                    } else {
                        Toast.makeText(AddTaskActivity.this, "添加任务失败", Toast.LENGTH_SHORT).show();
                        Log.e("AddTaskActivity", "任务添加到数据库失败");
                    }
                } else {
                    // 提示用户输入标题、内容并选择截止日期
                    Toast.makeText(AddTaskActivity.this, "请填写任务标题、内容并选择截止日期", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}