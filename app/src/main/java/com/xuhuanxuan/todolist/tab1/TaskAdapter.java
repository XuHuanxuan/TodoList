/** // 文件头注释：列表适配器职责
 * 简单任务列表适配器：使用系统 simple_list_item_2 展示标题与内容。
 */
package com.xuhuanxuan.todolist.tab1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuhuanxuan.todolist.dataclass.Task;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    
    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }
    
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);
        View view = convertView;
        
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        
        TextView titleView = view.findViewById(android.R.id.text1);
        TextView contentView = view.findViewById(android.R.id.text2);
        
        titleView.setText(task.getTitle());
        contentView.setText(task.getContent());
        
        return view;
    }
}