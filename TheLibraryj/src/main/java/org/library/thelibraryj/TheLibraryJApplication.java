package org.library.thelibraryj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication //(exclude = {SecurityAutoConfiguration.class})
@EnableAsync
@EnableJpaAuditing
public class TheLibraryJApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheLibraryJApplication.class, args);
	}

}
