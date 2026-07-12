package com.khronos.controller;

import com.khronos.service.ProjectService;
import com.khronos.service.TaskService;
import com.khronos.model.Project;
import com.khronos.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TasksController {

    @FXML
    private ComboBox<Project> projectCombo;

    @FXML
    private ComboBox<Task> taskCombo;

    @FXML
    private TextField newTaskField;

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> colTaskName;

    @FXML
    private TableColumn<Task, String> colTaskProject;

    private final TaskService taskService = new TaskService();
    private final ProjectService projectService = new ProjectService();

    private List<Task> allTasks = new ArrayList<>();

    @FXML
    public void initialize() {

        colTaskName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        colTaskProject.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProjectName()));

        projectCombo.valueProperty().addListener((obs, oldValue, newValue) -> refreshTaskCombo());

        carregarProjetos();
        carregarTarefas();

    }

    //Carrega todos os projetos.
    private void carregarProjetos() {

        try {

            List<Project> projetos = projectService.listarProjetos();

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

    // Carrega todas as tarefas.
    private void carregarTarefas() {

        try {

            allTasks = taskService.listarTarefas();

            taskTable.setItems(
                    FXCollections.observableArrayList(allTasks)
            );

            refreshTaskCombo();

        } catch (SQLException e) {

            mostrarErro(e.getMessage());

        }

    }

    //Atualiza a ComboBox de tarefas de acordo com o projeto.
    private void refreshTaskCombo() {

        Project projeto = projectCombo.getValue();

        List<Task> filtradas = new ArrayList<>();

        if (projeto != null) {

            for (Task t : allTasks) {

                if (t.getProjectId() == projeto.getId()) {

                    filtradas.add(t);

                }

            }

        }

        taskCombo.setItems(
                FXCollections.observableArrayList(filtradas)
        );

        if (!filtradas.isEmpty()) {

            taskCombo.getSelectionModel().selectFirst();

        }

    }

    // Criar uma nova tarefa.
    @FXML
    private void onAddTask() {

        String nome = newTaskField.getText().trim();

        Project projeto = projectCombo.getValue();

        if (nome.isBlank()) {

            mostrarErro("Digite o nome da tarefa.");

            return;

        }

        if (projeto == null) {

            mostrarErro("Selecione um projeto.");

            return;

        }

        try {

            taskService.cadastrarTarefa(nome, projeto.getId());

            newTaskField.clear();

            carregarTarefas();

        } catch (SQLException e) {

            mostrarErro("Erro ao cadastrar tarefa.\n\n" + e.getMessage());

        }

    }

    //Exclui a tarefa selecionada.
    @FXML
    private void onDeleteTask() {

        Task tarefa = taskCombo.getValue();

        if (tarefa == null) {

            mostrarErro("Selecione uma tarefa.");

            return;

        }

        try {

            taskService.excluirTarefa(tarefa.getId());

            carregarTarefas();

        } catch (SQLException e) {

            mostrarErro("Erro ao excluir tarefa.\n\n" + e.getMessage());

        }

    }

    //Retorna a tarefa selecionada.
    public Task getTaskSelecionada() {

        return taskCombo.getValue();

    }

    // Atualiza a lista de tarefas.
    public void atualizarTarefas() {

        carregarTarefas();

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
