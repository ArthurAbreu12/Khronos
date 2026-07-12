package com.khronos.controller;

import com.khronos.service.ProjectService;
import com.khronos.service.TaskService;
import com.khronos.service.TimeEntryService;
import com.khronos.model.Project;
import com.khronos.model.Task;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TimeEntryController {

    @FXML
    private ComboBox<Project> projectCombo;

    @FXML
    private ComboBox<Task> taskCombo;

    @FXML
    private Label timeLabel;

    @FXML
    private Label activeTaskLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    private final ProjectService projectService = new ProjectService();
    private final TaskService taskService = new TaskService();
    private final TimeEntryService timeEntryService = new TimeEntryService();

    private List<Task> allTasks = new ArrayList<>();

    private Timeline timeline;
    private LocalDateTime startTime;
    private Task currentTask;
    private boolean running = false;

    @FXML
    public void initialize() {

        carregarProjetos();

        projectCombo.valueProperty().addListener((obs, oldValue, newValue) -> atualizarComboTarefas());
    }

    private void carregarProjetos() {

        try {

            List<Project> projetos = projectService.listarProjetos();
            allTasks = taskService.listarTarefas();

            projectCombo.setItems(FXCollections.observableArrayList(projetos));

            if (!projetos.isEmpty()) {
                projectCombo.getSelectionModel().selectFirst();
            }

            atualizarComboTarefas();

        } catch (SQLException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarComboTarefas() {

        Project projeto = projectCombo.getValue();

        if (projeto == null) {
            taskCombo.getItems().clear();
            return;
        }

        List<Task> tarefas = new ArrayList<>();

        for (Task t : allTasks) {
            if (t.getProjectId() == projeto.getId()) {
                tarefas.add(t);
            }
        }

        taskCombo.setItems(FXCollections.observableArrayList(tarefas));

        if (!tarefas.isEmpty()) {
            taskCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void onStart() {

        if (running) {
            return;
        }

        Task tarefa = taskCombo.getValue();

        if (tarefa == null) {
            mostrarErro("Selecione uma tarefa.");
            return;
        }

        currentTask = tarefa;
        startTime = LocalDateTime.now();
        running = true;

        startButton.setDisable(true);
        stopButton.setDisable(false);

        projectCombo.setDisable(true);
        taskCombo.setDisable(true);

        activeTaskLabel.setText(tarefa.getName());

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> atualizarCronometro())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        atualizarCronometro();
    }

    @FXML
    private void onStop() {

        if (!running) {
            return;
        }

        LocalDateTime end = LocalDateTime.now();

        running = false;

        if (timeline != null) {
            timeline.stop();
        }

        startButton.setDisable(false);
        stopButton.setDisable(true);

        projectCombo.setDisable(false);
        taskCombo.setDisable(false);

        timeLabel.setText("00:00:00");
        activeTaskLabel.setText("Selecione um projeto e uma tarefa");

        try {

            timeEntryService.registrarHoras(
                    currentTask.getId(),
                    startTime,
                    end
            );

            currentTask = null;
            startTime = null;

        } catch (SQLException e) {

            mostrarErro(e.getMessage());

        }
    }

    private void atualizarCronometro() {

        long segundos = ChronoUnit.SECONDS.between(
                startTime,
                LocalDateTime.now()
        );

        timeLabel.setText(formatarTempo(segundos));
    }

    private String formatarTempo(long segundos) {

        long h = segundos / 3600;
        long m = (segundos % 3600) / 60;
        long s = segundos % 60;

        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void mostrarErro(String mensagem) {

        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                mensagem,
                ButtonType.OK
        );

        alert.setHeaderText(null);
        alert.setTitle("Khronos");
        alert.showAndWait();
    }
}