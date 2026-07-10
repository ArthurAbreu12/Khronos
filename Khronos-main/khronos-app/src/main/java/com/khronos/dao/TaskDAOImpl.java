package com.khronos.dao;

import com.khronos.db.Database;
import com.khronos.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAOImpl implements TaskDAO {
    private static final String BASE_SELECT =
            "SELECT t.id, t.name, p.id AS project_id, p.name AS project_name, p.color AS project_color " +
                    "FROM tasks t JOIN projects p ON p.id = t.project_id ";

    @Override
    public List<Task> findAll() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY t.id";
        List<Task> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        }
        return result;
    }

    @Override
    public List<Task> findByProject(int projectId) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.id = ? ORDER BY t.id";
        List<Task> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }
        return result;
    }

    @Override
    public Task insert(String name, int projectId) throws SQLException {
        String sql = "INSERT INTO tasks (name, project_id) VALUES (?, ?) RETURNING id";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int newId = rs.getInt("id");
                return findById(newId);
            }
        }
    }

    @Override
    public Task findById(int id) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return map(rs);
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Task map(ResultSet rs) throws SQLException {
        return new Task(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("project_id"),
                rs.getString("project_name"),
                rs.getString("project_color")
        );
    }
}
