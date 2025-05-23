package org.library.thelibraryj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ThymeleafAutoConfiguration.class})
@EnableAsync
@EnableScheduling
@EnableMethodSecurity(proxyTargetClass = true)
public class TheLibraryJApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheLibraryJApplication.class, args);
	}

}
