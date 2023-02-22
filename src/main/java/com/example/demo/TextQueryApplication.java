package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.example.demo.filter")
public class TextQueryApplication {
	 public static void main(String[] args) {
		 SpringApplication.run(TextQueryApplication.class, args);
	 }
}


/* Location:              D:\Spring\mvn_repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\TextQueryApplication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */