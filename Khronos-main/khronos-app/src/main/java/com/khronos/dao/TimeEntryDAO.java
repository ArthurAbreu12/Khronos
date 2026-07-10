package com.khronos.dao;

import com.khronos.model.TimeEntry;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TimeEntryDAO {

    TimeEntry insert(int taskId, LocalDateTime start, LocalDateTime end) throws SQLException;

    TimeEntry findById(int id) throws SQLException;

    List<TimeEntry> findRecent(int limit) throws SQLException;

    Map<Integer, Long> sumSecondsByTask() throws SQLException;

    Map<String, Long> sumSecondsByProject() throws SQLException;
}