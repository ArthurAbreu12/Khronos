package com.khronos.controller;

import com.khronos.model.Project;
import com.khronos.model.Task;
import com.khronos.service.ProjectService;
import com.khronos.service.TaskService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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

    @FXML
    private TableColumn<Task, String> colStartedAt;


    private final TaskService taskService = new TaskService();
    private final ProjectService projectService = new ProjectService();

    private List<Task> allTasks = new ArrayList<>();


    @FXML
    public void initialize() {

        carregarProjetos();

        carregarTarefas();

        colTaskName.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getName()
                )
        );


        colTaskProject.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getProjectName()
                )
        );


        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


        colStartedAt.setCellValueFactory(data -> {

            if (data.getValue().getStartedAt() == null) {

                return new SimpleStringProperty("-");

            }


            return new SimpleStringProperty(
                    data.getValue()
                            .getStartedAt()
                            .format(formatter)
            );

        });



        projectCombo.valueProperty().addListener(
                (obs, oldValue, newValue) ->
                        refreshTaskCombo()
        );


    }



    private void carregarProjetos() {

        try {

            List<Project> projetos =
                    projectService.listarProjetos();


            projectCombo.setItems(
                    FXCollections.observableArrayList(projetos)
            );


            if (!projetos.isEmpty()) {

                projectCombo.getSelectionModel()
                        .selectFirst();

            }


        } catch (SQLException e) {

            mostrarErro(e.getMessage());

        }

    }



    private void carregarTarefas() {

        try {

            allTasks =
                    taskService.listarTarefas();


            taskTable.setItems(
                    FXCollections.observableArrayList(allTasks)
            );


            refreshTaskCombo();


        } catch (SQLException e) {

            mostrarErro(e.getMessage());

        }

    }



    private void refreshTaskCombo() {


        Project projeto =
                projectCombo.getValue();


        List<Task> filtradas =
                new ArrayList<>();


        if (projeto != null) {


            for (Task task : allTasks) {


                if (task.getProjectId() == projeto.getId()) {

                    filtradas.add(task);

                }

            }

        }



        taskCombo.setItems(
                FXCollections.observableArrayList(filtradas)
        );


        if (!filtradas.isEmpty()) {

            taskCombo.getSelectionModel()
                    .selectFirst();

        }

    }



    @FXML
    private void onAddTask() {


        String nome =
                newTaskField.getText().trim();


        Project projeto =
                projectCombo.getValue();



        if (nome.isBlank()) {

            mostrarErro(
                    "Digite o nome da tarefa."
            );

            return;

        }



        if (projeto == null) {

            mostrarErro(
                    "Selecione um projeto."
            );

            return;

        }



        try {


            taskService.cadastrarTarefa(
                    nome,
                    projeto.getId()
            );


            newTaskField.clear();


            carregarTarefas();



        } catch (SQLException e) {


            mostrarErro(
                    "Erro ao cadastrar tarefa.\n\n"
                            + e.getMessage()
            );

        }

    }





    @FXML
    private void onDeleteTask() {


        Task tarefa =
                taskCombo.getValue();



        if (tarefa == null) {

            mostrarErro(
                    "Selecione uma tarefa."
            );

            return;

        }



        try {


            taskService.excluirTarefa(
                    tarefa.getId()
            );


            carregarTarefas();



        } catch (SQLException e) {


            mostrarErro(
                    "Erro ao excluir tarefa.\n\n"
                            + e.getMessage()
            );

        }

    }



    public Task getTaskSelecionada() {

        return taskCombo.getValue();

    }



    public void atualizarTarefas() {

        carregarTarefas();

    }

    public void atualizarProjetos() {

        carregarProjetos();

    }



    private void mostrarErro(String mensagem) {


        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle("Khronos");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);


        alert.showAndWait();

    }

}