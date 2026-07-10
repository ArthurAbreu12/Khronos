package com.khronos.dao;
import com.khronos.model.Project;

import java.sql.SQLException;
import java.util.List;
public interface ProjectDAO {
    List<Project> findAll() throws SQLException;

    Project insert(String name, String color) throws SQLException;

    void delete(int id) throws SQLException;
    }
