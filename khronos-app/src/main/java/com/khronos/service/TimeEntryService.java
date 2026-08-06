package com.khronos.service;

import com.khronos.dao.TimeEntryDAO;
import com.khronos.dao.TimeEntryDAOImpl;
import com.khronos.model.TimeEntry;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TimeEntryService {

    private final TimeEntryDAO dao = new TimeEntryDAOImpl();

    public TimeEntry registrarHoras(int taskId,
                                    LocalDateTime start,
                                    LocalDateTime end) throws SQLException {

        return dao.insert(taskId, start, end);
    }

    public TimeEntry buscarPorId(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<TimeEntry> listarRecentes(int limite) throws SQLException {
        return dao.findRecent(limite);
    }

    public Map<Integer, Long> totalSegundosPorTarefa() throws SQLException {
        return dao.sumSecondsByTask();
    }

    public Map<String, Long> totalSegundosPorProjeto() throws SQLException {
        return dao.sumSecondsByProject();
    }
}