/** // 文件头注释：说明此类职责与关键行为
 * 应用主界面：承载底部导航与三大功能 Fragment，并在首次启动时提示历史未完成任务。
 */
package com.xuhuanxuan.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import com.xuhuanxuan.todolist.tab1.AddTaskActivity;
import com.xuhuanxuan.todolist.tab1.HomeFragment;
import com.xuhuanxuan.todolist.utils.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 设置初始 Fragment：应用启动默认展示首页
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        loadFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.tab_time) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.tab_add_task){
                Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }

    // 加载选中的 Fragment：将容器中的内容替换为传入的 fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}