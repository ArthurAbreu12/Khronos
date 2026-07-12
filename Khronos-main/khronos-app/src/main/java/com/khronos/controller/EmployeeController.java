package com.khronos.controller;

import com.khronos.model.Employee;
import com.khronos.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class EmployeeController {

    private final EmployeeService service = new EmployeeService();

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtCargo;

    @FXML
    private TextField txtSalario;

    @FXML
    private TableView<Employee> tblEmployees;

    @FXML
    private TableColumn<Employee, Integer> colId;

    @FXML
    private TableColumn<Employee, String> colNome;

    @FXML
    private TableColumn<Employee, String> colCargo;

    @FXML
    private TableColumn<Employee, Double> colSalario;

    @FXML
    private TableColumn<Employee, Boolean> colAtivo;

    private Employee selecionado;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
        colAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));

        carregarFuncionarios();

        tblEmployees.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {

            if (novo != null) {
                selecionado = novo;

                txtNome.setText(novo.getNome());
                txtCargo.setText(novo.getCargo());
                txtSalario.setText(String.valueOf(novo.getSalario()));
            }
        });
    }

    @FXML
    private void onCadastrar() {

        try {

            service.adicionarFuncionario(
                    txtNome.getText(),
                    txtCargo.getText(),
                    Double.parseDouble(txtSalario.getText())
            );

            limparCampos();
            carregarFuncionarios();

        } catch (Exception e) {

            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void onAtualizar() {

        if (selecionado == null)
            return;

        try {

            selecionado.setNome(txtNome.getText());
            selecionado.setCargo(txtCargo.getText());
            selecionado.setSalario(Double.parseDouble(txtSalario.getText()));

            service.atualizarFuncionario(selecionado);

            limparCampos();
            carregarFuncionarios();

        } catch (Exception e) {

            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void onDemitir() {

        if (selecionado == null)
            return;

        try {

            service.demitirFuncionario(selecionado.getId());

            limparCampos();
            carregarFuncionarios();

        } catch (Exception e) {

            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void onLimpar() {

        limparCampos();
    }

    private void carregarFuncionarios() {

        try {

            tblEmployees.setItems(
                    FXCollections.observableArrayList(
                            service.listarFuncionarios()
                    )
            );

        } catch (SQLException e) {

            mostrarErro(e.getMessage());
        }
    }

    private void limparCampos() {

        txtNome.clear();
        txtCargo.clear();
        txtSalario.clear();

        selecionado = null;

        tblEmployees.getSelectionModel().clearSelection();
    }

    private void mostrarErro(String mensagem) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.showAndWait();
    }
}