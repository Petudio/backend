package kuding.petudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class PetudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetudioApplication.class, args);
	}

}
