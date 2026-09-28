package com.docsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocSyncAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocSyncAiApplication.class, args);
    }

}
