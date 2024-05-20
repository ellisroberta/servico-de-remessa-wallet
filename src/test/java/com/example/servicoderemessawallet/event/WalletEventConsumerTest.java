package com.example.servicoderemessawallet.event;

//import com.example.servicoderemessawallet.model.User;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WalletEventConsumerTest {

    @Mock
    private Logger logger;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletEventConsumer walletEventConsumer;

    @Test
    public void testProcessWalletEvent() {
        // Mensagem simulada recebida do RabbitMQ
        String message = "Test message";
//        User toUser = new User(); // Fazer fixture
//        toUser.setId(UUID.randomUUID());
//        toUser.setFullName("Jane Doe");

        // Simulando o recebimento da mensagem pelo consumidor
        walletEventConsumer.processWalletEvent(message/*, toUser*/);

        // Verificando se o logger registrou a mensagem recebida e o destinatário
        //verify(logger).info("Mensagem recebida: {}", message);
        //verify(logger).info("Destinatário: {}", toUser);

        // Verificando se o RabbitTemplate enviou a mensagem corretamente
        //verify(rabbitTemplate).convertAndSend("exchange-name", "routing-key", message);

        // Verificando se o transactionRepository.save foi chamado
        verify(transactionRepository).save(Mockito.any());
    }
}
