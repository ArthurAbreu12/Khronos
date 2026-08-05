package com.khronos.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centraliza a conexão JDBC com o PostgreSQL.
 * As credenciais ficam em src/main/resources/db.properties.
 */
public class Database {

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("Arquivo db.properties não encontrado no classpath.");
            }
            CONFIG.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar db.properties", e);
        }
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        String url = CONFIG.getProperty("db.url");
        String user = CONFIG.getProperty("db.user");
        String password = CONFIG.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}
