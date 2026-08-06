package com.khronos.service;

import com.khronos.dao.TaskDAO;
import com.khronos.dao.TaskDAOImpl;
import com.khronos.model.Task;

import java.sql.SQLException;
import java.util.List;

public class TaskService {

    private final TaskDAO taskDAO = new TaskDAOImpl();

    public List<Task> listarTarefas() throws SQLException {
        return taskDAO.findAll();
    }

    public Task buscarPorId(int id) throws SQLException {
        return taskDAO.findById(id);
    }

    public Task cadastrarTarefa(String nome, int projectId) throws SQLException {
        return taskDAO.insert(nome, projectId);
    }

    public void excluirTarefa(int id) throws SQLException {
        taskDAO.delete(id);
    }
}