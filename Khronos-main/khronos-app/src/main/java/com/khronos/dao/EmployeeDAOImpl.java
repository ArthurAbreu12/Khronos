package com.khronos.dao;

import com.khronos.db.Database;
import com.khronos.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT * FROM employees ORDER BY id";

        List<Employee> result = new ArrayList<>();

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
    public Employee findById(int id) throws SQLException {

        String sql = "SELECT * FROM employees WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return map(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Employee insert(String nome, String cargo, double salario) throws SQLException {

        String sql = """
                INSERT INTO employees(nome, cargo, salario, ativo)
                VALUES (?, ?, ?, true)
                RETURNING id
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, cargo);
            stmt.setDouble(3, salario);

            try (ResultSet rs = stmt.executeQuery()) {

                rs.next();

                return findById(rs.getInt("id"));
            }
        }
    }

    @Override
    public void demitir(int id) throws SQLException {

        String sql = """
                UPDATE employees
                SET ativo = false
                WHERE id = ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Employee map(ResultSet rs) throws SQLException {

        return new Employee(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cargo"),
                rs.getDouble("salario"),
                rs.getBoolean("ativo")
        );
    }
}
