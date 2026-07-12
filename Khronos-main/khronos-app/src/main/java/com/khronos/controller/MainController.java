package com.khronos.controller;

import com.khronos.db.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;

public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {

        try (Connection conn = Database.getConnection()) {

            if (conn != null && !conn.isClosed()) {
                statusLabel.setText("Banco conectado ✓");
            } else {
                statusLabel.setText("Banco desconectado");
            }

        } catch (Exception e) {

            statusLabel.setText("Erro ao conectar ao banco");

        }

    }

}