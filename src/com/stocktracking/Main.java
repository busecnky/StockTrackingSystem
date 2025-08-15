package com.stocktracking;

import com.stocktracking.config.DatabaseConfig;
import com.stocktracking.service.ProductService;
import com.stocktracking.service.ReportService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.createTable();
        ProductService productService = new ProductService(databaseConfig);
        ReportService reportService = new ReportService(productService);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Stok Takip Sistemi'ne Hoş Geldiniz!");

        while (true) {
            System.out.println("\nLütfen bir işlem seçin:");
            System.out.println("1. Ürün Ekle/Stok Güncelle");
            System.out.println("2. Stok Sorgula");
            System.out.println("3. Satış Yap");
            System.out.println("4. Ürün Sil");
            System.out.println("5. CSV Raporu Oluştur");
            System.out.println("6. Çıkış");
            System.out.print("   Seçiminiz: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Ürün adı: ");
                        String addName = scanner.nextLine();
                        System.out.print("Eklenecek miktar: ");
                        int addStock = Integer.parseInt(scanner.nextLine());
                        productService.addProduct(addName, addStock);
                        break;
                    case 2:
                        System.out.print("Sorgulanacak ürün adı: ");
                        String checkStockName = scanner.nextLine();
                        int stock = productService.checkStock(checkStockName);
                        if (stock != -1) {
                            System.out.println(checkStockName + " ürünü için mevcut stok: " + stock);
                        } else {
                            System.out.println(checkStockName + " ürünü bulunamadı.");
                        }
                        break;
                    case 3:
                        System.out.print("Satılacak ürün adı: ");
                        String sellName = scanner.nextLine();
                        System.out.print("Satış miktarı: ");
                        int sellQuantity = Integer.parseInt(scanner.nextLine());
                        try {
                            productService.sellProduct(sellName, sellQuantity);
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.println("Silinecek ürünün adı: ");
                        String deleteName = scanner.nextLine();
                        productService.deleteProduct(deleteName);
                        break;
                    case 5:
                        System.out.print("Rapor dosya adı: ");
                        String reportFilename = scanner.nextLine();
                        reportService.generateCSVReport(reportFilename);
                        break;
                    case 6:
                        System.out.println("Sistemden çıkış yapılıyor.!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Geçersiz seçim. Lütfen 1-6 arasında bir rakam girin.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Geçersiz giriş. Lütfen bir rakam girin.");
            }
        }
    }
}
