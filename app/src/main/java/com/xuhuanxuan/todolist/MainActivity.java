/** // 文件头注释：说明此类职责与关键行为
 * 应用主界面：承载底部导航与三大功能 Fragment，并在首次启动时提示历史未完成任务。
 */
package com.xuhuanxuan.todolist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

        // 设置初始 Fragment：应用启动默认展示首页
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
    }
}