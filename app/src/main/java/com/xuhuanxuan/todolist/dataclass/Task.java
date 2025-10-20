package com.xuhuanxuan.todolist.dataclass;

import java.util.List;

public class Task {
    private final long id; // 主键ID
    private String title; // 标题
    private String content; // 内容
    private String attachment; // 文件路径
    private long dueDate; // 截止时间（毫秒）
    private int importance; // 重要程度（1~N）
    private String category; // 分类
    private List<String> tags; // 标签集合
    private boolean isCompleted; // 是否完成

    public Task(long id, String title, String content, String attachment, long dueDate,
                int importance, String category, List<String> tags, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.attachment = attachment;
        this.dueDate = dueDate;
        this.importance = importance;
        this.category = category;
        this.tags = tags;
        this.isCompleted = isCompleted;
    }

    // Getter方法
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAttachment() {
        return attachment;
    }

    public long getDueDate() {
        return dueDate;
    }

    public int getImportance() {
        return importance;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

}