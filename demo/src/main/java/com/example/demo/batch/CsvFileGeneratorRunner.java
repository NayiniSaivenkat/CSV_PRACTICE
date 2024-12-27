package com.example.demo.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CsvFileGeneratorRunner implements CommandLineRunner {

    @Autowired
    private CsvFileService csvFileService;

    @Override
    public void run(String... args) throws Exception {
        String filePath = "data.csv";
        csvFileService.generateCsvFile(filePath);
    }
}
