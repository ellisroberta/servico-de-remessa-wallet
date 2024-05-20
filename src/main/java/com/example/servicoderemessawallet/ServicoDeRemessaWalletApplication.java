package com.example.servicoderemessawallet;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ServicoDeRemessaWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicoDeRemessaWalletApplication.class, args);
	}

}
