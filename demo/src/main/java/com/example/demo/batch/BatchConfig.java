package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Date;

@Configuration
@EnableBatchProcessing
public class BatchConfig {


    private final DataSource dataSource;
    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        FileSystemResource resource = new FileSystemResource("D:\\CSV_CRUD1\\data.csv");

        if (!resource.exists()) {
            throw new IllegalArgumentException("The file 'data.csv' does not exist at the specified location.");
        }

        reader.setResource(resource);
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        System.out.println("FlatFileItemReader has been configured to read data from 'data.csv'.");
        reader.setSaveState(false); // Disable state saving to make it easier to debug

        reader.setSkippedLinesCallback(line -> {
            System.out.println("Skipping line: " + line);
        });

        return reader;
    }

    @Bean
    public LineMapper<Employee> lineMapper() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "salary", "dept", "joiningDate", "address", "city", "state", "email", "phone");

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class); // Specify the target type (Employee)

        // Return a LineMapper that uses the tokenizer and the field mapper to populate the Employee object
        return new DefaultLineMapper<Employee>() {{
            setLineTokenizer(tokenizer);
            setFieldSetMapper(fieldSetMapper);
        }};
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer(DataSource dataSource) {
        JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO employees (id, name, salary, dept, joining_date, address, city, state, email, phone) " +
                "VALUES (:id, :name, :salary, :dept, :joiningDate, :address, :city, :state, :email, :phone)");
        writer.setDataSource(dataSource);

        // Add logging to ensure it is writing the data

        System.out.println(writer);
        return writer;
    }


    @Bean
    public Step step() {
        // Create StepBuilder manually using JobRepository
        StepBuilder stepBuilder = new StepBuilder("step", jobRepository);

        // Configure the Step with reader, writer, and chunk size
        return stepBuilder
                .<Employee, Employee>chunk(100,transactionManager)
                .reader(reader())
                .writer(writer(null))
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
}




