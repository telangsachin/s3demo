package com.example.aws.s3example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.example.aws.s3example")
@SpringBootApplication

public class S3exampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3exampleApplication.class, args);
	}

}
