package com.khronos.model;

import java.time.LocalDateTime;

public class Task {

    private final int id;
    private final String name;
    private final int projectId;
    private final String projectName;
    private final String projectColor;

    private LocalDateTime startedAt;


    public Task(
            int id,
            String name,
            int projectId,
            String projectName,
            String projectColor,
            LocalDateTime startedAt
    ) {

        this.id = id;
        this.name = name;
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectColor = projectColor;
        this.startedAt = startedAt;

    }



    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public int getProjectId() {
        return projectId;
    }


    public String getProjectName() {
        return projectName;
    }


    public String getProjectColor() {
        return projectColor;
    }


    public LocalDateTime getStartedAt() {
        return startedAt;
    }


    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }



    @Override
    public String toString() {

        return name;

    }

}