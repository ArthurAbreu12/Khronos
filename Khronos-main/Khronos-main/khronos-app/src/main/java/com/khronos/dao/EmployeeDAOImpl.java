package com.khronos.dao;

import com.khronos.db.Database;
import com.khronos.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public List<Employee> findAll() throws SQLException {

        String sql = "SELECT id, nome, cargo, salario, ativo FROM employees ORDER BY id";

        List<Employee> employees = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(map(rs));
            }
        }

        return employees;
    }

    @Override
    public Employee findById(int id) throws SQLException {

        String sql = "SELECT id, nome, cargo, salario, ativo FROM employees WHERE id = ?";

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
                INSERT INTO employees (nome, cargo, salario, ativo)
                VALUES (?, ?, ?, true)
                RETURNING id
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, cargo);
            stmt.setDouble(3, salario);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new Employee(
                            rs.getInt("id"),
                            nome,
                            cargo,
                            salario,
                            true
                    );
                }
            }
        }

        return null;
    }

    @Override
    public void update(Employee employee) throws SQLException {

        String sql = """
                UPDATE employees
                   SET nome = ?,
                       cargo = ?,
                       salario = ?,
                       ativo = ?
                 WHERE id = ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getNome());
            stmt.setString(2, employee.getCargo());
            stmt.setDouble(3, employee.getSalario());
            stmt.setBoolean(4, employee.isAtivo());
            stmt.setInt(5, employee.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void demitir(int id) throws SQLException {

        String sql = "UPDATE employees SET ativo = false WHERE id = ?";

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