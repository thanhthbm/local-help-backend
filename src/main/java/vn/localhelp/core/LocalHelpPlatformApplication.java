package vn.localhelp.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LocalHelpPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalHelpPlatformApplication.class, args);
	}

}
