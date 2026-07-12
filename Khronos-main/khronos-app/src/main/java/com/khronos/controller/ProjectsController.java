package com.khronos.controller;

import com.khronos.service.ProjectService;
import com.khronos.model.Project;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class ProjectsController {

    private static final String[] PALETTE = {
            "#e8a33d",
            "#4ddac0",
            "#d9634d",
            "#8ab6e8",
            "#c98ee8"
    };

    @FXML
    private ComboBox<Project> projectCombo;

    @FXML
    private TextField newProjectField;

    private final ProjectService service = new ProjectService();

    @FXML
    public void initialize() {
        carregarProjetos();
    }

    /**
     * Carrega todos os projetos do banco.
     */
    public void carregarProjetos() {

        try {

            List<Project> projetos = service.listarProjetos();

            projectCombo.setItems(
                    FXCollections.observableArrayList(projetos)
            );

            if (!projetos.isEmpty()) {
                projectCombo.getSelectionModel().selectFirst();
            }

        } catch (SQLException e) {
            mostrarErro(e.getMessage());
        }

    }

    //Adiciona um novo projeto.
    @FXML
    private void onAddProject() {

        String nome = newProjectField.getText().trim();

        if (nome.isBlank()) {
            mostrarErro("Digite o nome do projeto.");
            return;
        }

        String cor = PALETTE[
                projectCombo.getItems().size() % PALETTE.length
                ];

        try {

            service.criarProjeto(nome, cor);

            newProjectField.clear();

            carregarProjetos();

        } catch (SQLException e) {

            mostrarErro("Erro ao cadastrar projeto.\n\n" + e.getMessage());

        }

    }

    //Remove um projeto.
    @FXML
    private void onDeleteProject() {

        Project projeto = projectCombo.getValue();

        if (projeto == null) {

            mostrarErro("Selecione um projeto.");

            return;
        }

        try {

            service.excluirProjeto(projeto.getId());

            carregarProjetos();

        } catch (SQLException e) {

            mostrarErro("Erro ao excluir projeto.\n\n" + e.getMessage());

        }

    }

    // Retorna o projeto atualmente selecionado.
    public Project getProjetoSelecionado() {

        return projectCombo.getValue();

    }

    //Atualiza a ComboBox.
    public void atualizarProjetos() {

        carregarProjetos();

    }

    //Exibe mensagens de erro.
    private void mostrarErro(String mensagem) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Khronos");

        alert.setHeaderText(null);

        alert.setContentText(mensagem);

        alert.showAndWait();

    }

}
