package com.khronos.dao;

import com.khronos.db.Database;
import com.khronos.model.TimeEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimeEntryDAOImpl {
    public TimeEntry insert(int taskId, LocalDateTime start, LocalDateTime end) throws SQLException {
        long durationSeconds = java.time.Duration.between(start, end).getSeconds();
        String sql = "INSERT INTO time_entries (task_id, start_time, end_time, duration_seconds) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));
            stmt.setLong(4, durationSeconds);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int id = rs.getInt("id");
                return findById(id);
            }
        }
    }

    public TimeEntry findById(int id) throws SQLException {
        String sql = "SELECT te.id, te.task_id, t.name AS task_name, p.name AS project_name, " +
                "te.start_time, te.end_time, te.duration_seconds " +
                "FROM time_entries te " +
                "JOIN tasks t ON t.id = te.task_id " +
                "JOIN projects p ON p.id = t.project_id " +
                "WHERE te.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return map(rs);
            }
        }
    }

    public List<TimeEntry> findRecent(int limit) throws SQLException {
        String sql = "SELECT te.id, te.task_id, t.name AS task_name, p.name AS project_name, " +
                "te.start_time, te.end_time, te.duration_seconds " +
                "FROM time_entries te " +
                "JOIN tasks t ON t.id = te.task_id " +
                "JOIN projects p ON p.id = t.project_id " +
                "ORDER BY te.end_time DESC LIMIT ?";
        List<TimeEntry> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }
        return result;
    }

    /** Retorna o total de segundos apontados por tarefa (task_id -> segundos). */
    public Map<Integer, Long> sumSecondsByTask() throws SQLException {
        String sql = "SELECT task_id, COALESCE(SUM(duration_seconds), 0) AS total " +
                "FROM time_entries GROUP BY task_id";
        Map<Integer, Long> result = new LinkedHashMap<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getInt("task_id"), rs.getLong("total"));
            }
        }
        return result;
    }

    /** Retorna, em ordem, o total de segundos apontados por projeto (nome -> segundos). */
    public Map<String, Long> sumSecondsByProject() throws SQLException {
        String sql = "SELECT p.name, COALESCE(SUM(te.duration_seconds), 0) AS total " +
                "FROM projects p " +
                "LEFT JOIN tasks t ON t.project_id = p.id " +
                "LEFT JOIN time_entries te ON te.task_id = t.id " +
                "GROUP BY p.id, p.name ORDER BY p.id";
        Map<String, Long> result = new LinkedHashMap<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getLong("total"));
            }
        }
        return result;
    }

    private TimeEntry map(ResultSet rs) throws SQLException {
        return new TimeEntry(
                rs.getInt("id"),
                rs.getInt("task_id"),
                rs.getString("task_name"),
                rs.getString("project_name"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getLong("duration_seconds")
        );
    }
}
