package com.khronos.service;

import com.khronos.dao.ProjectDAO;
import com.khronos.dao.ProjectDAOImpl;
import com.khronos.model.Project;

import java.sql.SQLException;
import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAOImpl();
    }

    //Retorna todos os projetos.

    public List<Project> listarProjetos() throws SQLException {
        return projectDAO.findAll();
    }

    //Cria um novo projeto.

    public void criarProjeto(String nome, String cor) throws SQLException {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do projeto é obrigatório.");
        }

        projectDAO.insert(nome.trim(), cor);
    }

    //Atualiza um projeto.

    public void atualizarProjeto(Project projeto) throws SQLException {

        if (projeto == null) {
            throw new IllegalArgumentException("Projeto inválido.");
        }

        projectDAO.update(projeto);
    }

    // Remove um projeto
    public void excluirProjeto(int id) throws SQLException {
        projectDAO.delete(id);
    }

    // Busca um projeto pelo ID.
    public Project buscarProjeto(int id) throws SQLException {
        return projectDAO.findById(id);
    }

}