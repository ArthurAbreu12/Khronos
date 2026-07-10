package com.khronos.dao;

import com.khronos.model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDAO {

    List<Employee> findAll() throws SQLException;

    Employee findById(int id) throws SQLException;

    Employee insert(String nome, String cargo, double salario) throws SQLException;

    void demitir(int id) throws SQLException;

}
