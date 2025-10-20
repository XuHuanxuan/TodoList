/** // 文件头注释：Fragment 作用
 * 今日任务首页：展示当天任务列表，支持跳转新增与详情。
 */
package com.xuhuanxuan.todolist.tab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.xuhuanxuan.todolist.R;
import com.xuhuanxuan.todolist.dataclass.Task;
import com.xuhuanxuan.todolist.utils.DatabaseHelper;


public class HomeFragment extends Fragment {

    private TaskAdapter taskAdapter;
    private DatabaseHelper db;
    private ListView taskListView;
    private Button addTaskButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { // 参数：布局填充器/容器/状态
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化数据库和视图
        db = new DatabaseHelper(requireContext());
        taskListView = view.findViewById(R.id.taskListView);
        addTaskButton = view.findViewById(R.id.addTaskButton);

        // 加载任务列表
        loadTasks();

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到新增任务页面
                Intent intent = new Intent(requireContext(), AddTaskActivity.class);
                startActivity(intent);
            }
        });

        // 设置任务列表的点击事件，跳转到各个任务的详情页
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = taskAdapter.getItem(position);
                if (selectedTask != null) {
                    Intent intent = new Intent(requireContext(), TaskDetailActivity.class);
                    intent.putExtra("TASK_ID", selectedTask.getId());
                    intent.putExtra("TASK_TITLE", selectedTask.getTitle());
                    intent.putExtra("TASK_CONTENT", selectedTask.getContent());
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "未找到选中的任务", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadTasks() { // 加载任务列表
        java.util.List<Task> tasks = db.getAllTasks();
        taskAdapter = new TaskAdapter(requireContext(), tasks);
        taskListView.setAdapter(taskAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }
}