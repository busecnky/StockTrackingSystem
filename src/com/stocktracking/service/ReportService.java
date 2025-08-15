package com.stocktracking.service;

import com.stocktracking.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportService {
    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(ReportService.class);


    public ReportService(ProductService productService) {
        this.productService = productService;
    }

    public void generateCSVReport(String filename) {
        List<Product> products = productService.getAllProducts();

        try (FileWriter writer = new FileWriter(filename + ".csv")) {
            writer.append("ID, Ürün Adı, Stok\n");
            for (Product product : products) {
                writer.append(String.format("%d,  \"%s\",  %d\n",
                        product.id(), product.name(), product.stock()));
            }
            System.out.println("Rapor başarıyla " + filename + " dosyasına yazıldı.");
        } catch (IOException e) {
            logger.error("CSV raporu oluşturulurken hata oluştu: " + e.getMessage());
        }
    }
}
