package org.hhplus.speciallecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpecialLectureApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpecialLectureApplication.class, args);
	}

}
