package com.khronos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeEntry {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final int id;
    private final int taskId;
    private final String taskName;
    private final String projectName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final long durationSeconds;

    public TimeEntry(int id, int taskId, String taskName, String projectName,
                      LocalDateTime startTime, LocalDateTime endTime, long durationSeconds) {
        this.id = id;
        this.taskId = taskId;
        this.taskName = taskName;
        this.projectName = projectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
    }

    public int getId() { return id; }
    public int getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getProjectName() { return projectName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public long getDurationSeconds() { return durationSeconds; }

    public String getEndFormatted() {
        return endTime.format(TIME_FMT);
    }

    public String getDurationFormatted() {
        long h = durationSeconds / 3600;
        long m = (durationSeconds % 3600) / 60;
        long s = durationSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
