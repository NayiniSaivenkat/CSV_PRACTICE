package com.example.demo.batch;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class CsvFileService {

    public void generateCsvFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the header
            writer.append("ID,Name,Salary,Dept,Address,City,State,Email,Phone\n");

            // Generate 10,000 lines of data
            for (int i = 1; i <= 100; i++) {
                writer.append(i + ",")
                        .append("Name" + i + ",")
                        .append((5000 + (i % 100)) + ",")
                        .append("Dept" + (i % 10) + ",")
                        .append("Address" + i + ",")
                        .append("City" + (i % 100) + ",")
                        .append("State" + (i % 50) + ",")
                        .append("user" + i + "@example.com,")
                        .append("123456789" + (i % 10))
                        .append("\n");
            }
            System.out.println("CSV File Generated: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}