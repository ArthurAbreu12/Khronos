package com.khronos.service;

import com.khronos.dao.EmployeeDAO;
import com.khronos.dao.EmployeeDAOImpl;
import com.khronos.model.Employee;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    public List<Employee> listarFuncionarios() throws SQLException {
        return employeeDAO.findAll();
    }

    public Employee buscarPorId(int id) throws SQLException {
        return employeeDAO.findById(id);
    }

    public Employee adicionarFuncionario(String nome, String cargo, double salario) throws SQLException {
        return employeeDAO.insert(nome, cargo, salario);
    }

    public void atualizarFuncionario(Employee employee) throws SQLException {
        employeeDAO.update(employee);
    }

    public void demitirFuncionario(int id) throws SQLException {
        employeeDAO.demitir(id);
    }
}