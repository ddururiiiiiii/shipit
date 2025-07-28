package io.github.seulgi.shipit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShipitApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipitApplication.class, args);
	}

}
