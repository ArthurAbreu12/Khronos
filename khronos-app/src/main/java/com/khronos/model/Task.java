package com.khronos.model;

public class Task {

    private final int id;
    private final String name;
    private final int projectId;
    private final String projectName;
    private final String projectColor;

    public Task(int id, String name, int projectId, String projectName, String projectColor) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectColor = projectColor;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getProjectId() { return projectId; }
    public String getProjectName() { return projectName; }
    public String getProjectColor() { return projectColor; }

    @Override
    public String toString() {
        return name;
    }
}
