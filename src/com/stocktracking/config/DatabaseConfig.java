package com.stocktracking.config;

import com.stocktracking.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String URL = "jdbc:sqlite:stocktracking.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void createTable() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS products ("
                    + "id INTEGER PRIMARY KEY,"
                    + "name TEXT NOT NULL UNIQUE,"
                    + "stock INTEGER NOT NULL CHECK(stock >= 0)"
                    + ");";
            statement.executeUpdate(sql);
            System.out.println("Ürünler tablosu başarıyla oluşturuldu.");
        } catch (SQLException e) {
            logger.error("Veritabanı tablosu oluşturulurken hata oluştu: " + e.getMessage());
        }
    }
}