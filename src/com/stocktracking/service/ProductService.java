package com.stocktracking.service;

import com.stocktracking.config.DatabaseConfig;
import com.stocktracking.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final DatabaseConfig databaseConfig;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void addProduct(String addName, int addStock) {
        String sql = "INSERT OR REPLACE INTO products (name, stock) " +
                "VALUES (?, COALESCE((SELECT stock FROM products WHERE name = ?), 0) + ?)";
        try (Connection connection = databaseConfig.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, addName);
            preparedStatement.setString(2, addName);
            preparedStatement.setInt(3, addStock);
            preparedStatement.executeUpdate();
            System.out.println(addName + " ürününe " + addStock + " adet eklendi.");
        } catch (SQLException e) {
            logger.error("Ürün eklenirken hata oluştu: " + e.getMessage());
            System.err.println();
        }
    }

    public int checkStock(String checkStockName) {
        String sql = "SELECT stock FROM products WHERE name = ?";
        try (Connection connection = databaseConfig.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, checkStockName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("stock");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Stok sorgulanırken hata oluştu: " + e.getMessage());
            logger.error("Stok sorgulanırken hata oluştu: " + e.getMessage());

            return -1;
        }
    }

    public synchronized void sellProduct(String sellName, int sellQuantity) throws IllegalArgumentException {
        String updateSql = "UPDATE products SET stock = stock - ? WHERE name = ?";

        try (Connection connection = databaseConfig.connect()) {
            connection.setAutoCommit(false);

            int currentStock = checkStock(sellName);

            if (currentStock == -1) {
                connection.rollback();
                throw new IllegalArgumentException("Hata: " + sellName + " ürünü bulunamadı.");
            }

            if (currentStock < sellQuantity) {
                connection.rollback();
                throw new IllegalArgumentException("Hata: " + sellName + " ürünü için yeterli stok yok. Mevcut stok: " + currentStock);
            }

            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setInt(1, sellQuantity);
                pstmt.setString(2, sellName);
                pstmt.executeUpdate();
            }

            connection.commit();
            System.out.println(sellName + " ürününden " + sellQuantity + " adet satıldı.");

        } catch (SQLException e) {
            logger.error("Satış işlemi sırasında veritabanı hatası oluştu: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, stock FROM products";
        try (Connection connection = databaseConfig.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                products.add(new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ürünler listelenirken hata oluştu: " + e.getMessage());
            logger.error("Ürünler listelenirken hata oluştu: " + e.getMessage());

        }
        return products;
    }

    public void deleteProduct(String deleteName) {
        String sql = "DELETE FROM products WHERE name = ?";
        try(Connection connection = databaseConfig.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1,deleteName);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                System.out.println(deleteName + " ürünü başarıyla silindi.");
            }else{
                System.out.println("Hata: " + deleteName + " adında bir ürün bulunamadı.");
            }
        } catch (SQLException e) {
            logger.error("Ürün silinirken hata oluştu: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
