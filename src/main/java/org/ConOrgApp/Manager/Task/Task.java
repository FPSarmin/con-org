package org.ConOrgApp.Manager.Task;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class Task {
    private int id;
    private String description;
    private Date deadline;
    private Boolean complete = Boolean.FALSE;
    private int priority;

    private String category = "default";

    public Task(int id, String description, Date deadline, int priority) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
    }

    public Task(String description, Date deadline, int priority) {
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
    }

    public Task(String description, Date deadline, int priority, String category) {
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.category = category;
    }

    public Task(int id, String description, Date deadline, boolean complete, int priority, String category) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.complete = complete;
        this.category = category;
    }

    public Task(int id, String description, Date deadline, boolean complete, int priority) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.complete = complete;
        this.category = "default";
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public Boolean isComplete() {
        return complete;
    }

    public int getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setComplete() {
        this.complete = Boolean.TRUE;
    }

    public void setIncomplete() {
        this.complete = Boolean.FALSE;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void display() {
        String isDone = StringUtils.repeat("YES", complete ? 1 : 0) + StringUtils.repeat("NO", complete ? 0 : 1);
        System.out.printf("## Id          :%s%s ##\n", StringUtils.repeat(' ', 59-Integer.toString(id).length()), id);
        System.out.printf("## Category    :%s%s ##\n", StringUtils.repeat(' ', 59-category.length()), category);
        System.out.printf("## Description :%s%s ##\n", StringUtils.repeat(' ', 59-description.length()), description);
        System.out.printf("## Deadline    :%s%s ##\n", StringUtils.repeat(' ', 59-deadline.toString().length()), deadline.toString());
        System.out.printf("## Complete    :%s%s ##\n", StringUtils.repeat(' ', 59-isDone.length()), isDone);
        System.out.printf("## Priority    :%s%s ##\n", StringUtils.repeat(' ', 59-Integer.toString(priority).length()), priority);
    }
}
