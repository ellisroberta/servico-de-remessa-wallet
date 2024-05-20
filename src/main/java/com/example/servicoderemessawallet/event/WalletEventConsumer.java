package com.example.servicoderemessawallet.event;

import com.example.servicoderemessawallet.model.Transaction;
//import com.example.servicoderemessawallet.model.User;
import com.example.servicoderemessawallet.repository.TransactionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

@Component
public class WalletEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(WalletEventConsumer.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @RabbitListener(queues = "queue-name")
    public void processWalletEvent(String message /*User toUser*/) {
        try {
            // Processar o evento recebido do RabbitMQ
            logger.info("Mensagem recebida: {}", message);
            //logger.info("Destinatário: {}", toUser);

            // Aqui você pode adicionar lógica para processar a mensagem, como persistir em banco de dados ou chamar serviços
            // Exemplo: service.processEvent(message);
            // Exemplo de persistência em banco de dados
            Transaction transaction = new Transaction();
            //transaction.setToUser(toUser);
            // Defina outros campos da transação conforme necessário
            transactionRepository.save(transaction);
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem do RabbitMQ: {}", message, e);
            // Você pode lançar uma exceção personalizada ou lidar com o erro de outra forma, dependendo dos requisitos
        }
    }
}
