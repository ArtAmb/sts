package psk.isf.sts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableOAuth2Sso
public class StsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StsApplication.class, args);
	}
}
