package br.com.flightOnTime;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "API - Análise de previsão de de vôos",
				version = "1.0",
				description = "API para análise de previsão de vôos",
				contact = @Contact()
		)
)
public class FlightOnTimeJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightOnTimeJavaApplication.class, args);
	}

}
