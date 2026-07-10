package com.khronos.dao;

import com.khronos.db.Database;
import com.khronos.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAOImpl implements ProjectDAO {

    @Override
    public List<Project> findAll() throws SQLException {
        String sql = "SELECT id, name, color FROM projects ORDER BY id";
        List<Project> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(new Project(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("color")
                ));
            }
        }

        return result;
    }

    @Override
    public Project insert(String name, String color) throws SQLException {
        String sql = "INSERT INTO projects (name, color) VALUES (?, ?) RETURNING id";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, color);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return new Project(rs.getInt("id"), name, color);
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}