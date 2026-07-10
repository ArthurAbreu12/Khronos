package com.khronos.dao;

import com.khronos.model.Task;

import java.sql.SQLException;
import java.util.List;

public interface TaskDAO {

    List<Task> findAll() throws SQLException;

    List<Task> findByProject(int projectId) throws SQLException;

    Task insert(String name, int projectId) throws SQLException;

    Task findById(int id) throws SQLException;

    void delete(int id) throws SQLException;
}