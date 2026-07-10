package com.khronos.controller;

import com.khronos.dao.ProjectDAO;
import com.khronos.dao.ProjectDAOImpl;
import com.khronos.dao.TaskDAO;
import com.khronos.dao.TaskDAOImpl;
import com.khronos.dao.TimeEntryDAO;
import com.khronos.dao.TimeEntryDAOImpl;
import com.khronos.model.Project;
import com.khronos.model.Task;
import com.khronos.model.TimeEntry;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainController {

    private static final String[] PALETTE = {"#e8a33d", "#4ddac0", "#d9634d", "#8ab6e8", "#c98ee8"};

    // ---------- FXML ----------
    @FXML private Label statusLabel;
    @FXML private ComboBox<Project> projectCombo;
    @FXML private ComboBox<Task> taskCombo;
    @FXML private Label timeLabel;
    @FXML private Label activeTaskLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;

    @FXML private TextField newProjectField;
    @FXML private TextField newTaskField;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colTaskName;
    @FXML private TableColumn<Task, String> colTaskProject;
    @FXML private TableColumn<Task, String> colTaskTotal;

    @FXML private VBox reportBox;

    @FXML private TableView<TimeEntry> historyTable;
    @FXML private TableColumn<TimeEntry, String> colHistTask;
    @FXML private TableColumn<TimeEntry, String> colHistProject;
    @FXML private TableColumn<TimeEntry, String> colHistWhen;
    @FXML private TableColumn<TimeEntry, String> colHistDuration;

    // ---------- DAOs ----------
    private final ProjectDAO projectDao = new ProjectDAOImpl();
    private final TaskDAO taskDao = new TaskDAOImpl();
    private final TimeEntryDAOImpl timeEntryDao = new TimeEntryDAOImpl();

    // ---------- Estado ----------
    private List<Task> allTasks = new ArrayList<>();
    private Map<Integer, Long> taskTotals = Map.of();

    private boolean running = false;
    private LocalDateTime startTime;
    private Task currentTask;
    private Timeline timeline;

    @FXML
    public void initialize() {
        colTaskName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colTaskProject.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProjectName()));
        colTaskTotal.setCellValueFactory(data -> {
            long secs = taskTotals.getOrDefault(data.getValue().getId(), 0L);
            return new SimpleStringProperty(formatSeconds(secs));
        });

        colHistTask.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTaskName()));
        colHistProject.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProjectName()));
        colHistWhen.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndFormatted()));
        colHistDuration.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDurationFormatted()));

        projectCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshTaskCombo());

        loadAll();
    }

    // ---------- Carregamento ----------
    private void loadAll() {
        try {
            List<Project> projects = projectDao.findAll();
            allTasks = taskDao.findAll();
            taskTotals = timeEntryDao.sumSecondsByTask();
            List<TimeEntry> recent = timeEntryDao.findRecent(20);
            Map<String, Long> byProject = timeEntryDao.sumSecondsByProject();

            Project keepSelected = projectCombo.getValue();
            ObservableList<Project> projectItems = FXCollections.observableArrayList(projects);
            projectCombo.setItems(projectItems);
            if (keepSelected != null && projects.stream().anyMatch(p -> p.getId() == keepSelected.getId())) {
                projectCombo.getSelectionModel().select(
                    projects.stream().filter(p -> p.getId() == keepSelected.getId()).findFirst().orElse(null));
            } else if (!projects.isEmpty()) {
                projectCombo.getSelectionModel().selectFirst();
            }

            refreshTaskCombo();

            taskTable.setItems(FXCollections.observableArrayList(allTasks));
            taskTable.refresh();

            historyTable.setItems(FXCollections.observableArrayList(recent));

            renderReport(byProject);

            statusLabel.setText("conectado ao banco ✓");
            statusLabel.getStyleClass().removeAll("status-error");
            if (!statusLabel.getStyleClass().contains("status-ok")) {
                statusLabel.getStyleClass().add("status-ok");
            }
        } catch (SQLException e) {
            statusLabel.setText("sem conexão com o banco: " + e.getMessage());
            statusLabel.getStyleClass().removeAll("status-ok");
            if (!statusLabel.getStyleClass().contains("status-error")) {
                statusLabel.getStyleClass().add("status-error");
            }
        }
    }

    private void refreshTaskCombo() {
        Project selectedProject = projectCombo.getValue();
        List<Task> filtered = new ArrayList<>();
        if (selectedProject != null) {
            for (Task t : allTasks) {
                if (t.getProjectId() == selectedProject.getId()) {
                    filtered.add(t);
                }
            }
        }
        Task keepTask = taskCombo.getValue();
        taskCombo.setItems(FXCollections.observableArrayList(filtered));
        if (keepTask != null && filtered.stream().anyMatch(t -> t.getId() == keepTask.getId())) {
            taskCombo.getSelectionModel().select(
                filtered.stream().filter(t -> t.getId() == keepTask.getId()).findFirst().orElse(null));
        } else if (!filtered.isEmpty()) {
            taskCombo.getSelectionModel().selectFirst();
        } else {
            taskCombo.getSelectionModel().clearSelection();
        }
    }

    private void renderReport(Map<String, Long> byProject) {
        reportBox.getChildren().clear();
        if (byProject.isEmpty()) {
            Label empty = new Label("Ainda não há projetos cadastrados.");
            empty.getStyleClass().add("timer-sublabel");
            reportBox.getChildren().add(empty);
            return;
        }
        long max = byProject.values().stream().mapToLong(Long::longValue).max().orElse(1L);
        if (max == 0) max = 1L;

        for (Map.Entry<String, Long> entry : byProject.entrySet()) {
            Label nameLabel = new Label(entry.getKey());
            nameLabel.getStyleClass().add("report-row-name");

            Label timeLbl = new Label(formatSeconds(entry.getValue()));
            timeLbl.getStyleClass().add("report-row-time");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox top = new HBox(nameLabel, spacer, timeLbl);
            top.setAlignment(Pos.CENTER_LEFT);

            ProgressBar bar = new ProgressBar((double) entry.getValue() / max);
            bar.getStyleClass().add("report-bar");
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setPrefHeight(10);

            VBox row = new VBox(6, top, bar);
            reportBox.getChildren().add(row);
        }
    }

    // ---------- Ações ----------
    @FXML
    private void onAddProject() {
        String name = newProjectField.getText() == null ? "" : newProjectField.getText().trim();
        if (name.isEmpty()) return;
        String color = PALETTE[projectCombo.getItems().size() % PALETTE.length];
        try {
            projectDao.insert(name, color);
            newProjectField.clear();
            loadAll();
        } catch (SQLException e) {
            showError("Não foi possível criar o projeto: " + e.getMessage());
        }
    }

    @FXML
    private void onAddTask() {
        String name = newTaskField.getText() == null ? "" : newTaskField.getText().trim();
        Project selected = projectCombo.getValue();
        if (name.isEmpty() || selected == null) {
            showError("Selecione um projeto e digite o nome da tarefa.");
            return;
        }
        try {
            taskDao.insert(name, selected.getId());
            newTaskField.clear();
            loadAll();
        } catch (SQLException e) {
            showError("Não foi possível criar a tarefa: " + e.getMessage());
        }
    }

    @FXML
    private void onStart() {
        Task task = taskCombo.getValue();
        if (task == null) {
            showError("Cadastre e selecione uma tarefa antes de iniciar.");
            return;
        }
        currentTask = task;
        startTime = LocalDateTime.now();
        running = true;

        startButton.setDisable(true);
        stopButton.setDisable(false);
        projectCombo.setDisable(true);
        taskCombo.setDisable(true);
        activeTaskLabel.setText(task.getName());

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        tick();
    }

    @FXML
    private void onStop() {
        if (!running) return;
        LocalDateTime end = LocalDateTime.now();
        timeline.stop();
        running = false;

        startButton.setDisable(false);
        stopButton.setDisable(true);
        projectCombo.setDisable(false);
        taskCombo.setDisable(false);
        timeLabel.setText("00:00:00");
        activeTaskLabel.setText("Selecione um projeto e uma tarefa");

        try {
            timeEntryDao.insert(currentTask.getId(), startTime, end);
            loadAll();
        } catch (SQLException e) {
            showError("Não foi possível salvar o registro: " + e.getMessage());
        }
    }

    private void tick() {
        long seconds = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
        timeLabel.setText(formatSeconds(seconds));
    }

    private String formatSeconds(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Khronos");
        alert.showAndWait();
    }
}
