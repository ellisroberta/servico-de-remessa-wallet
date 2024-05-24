package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.exception.ExchangeRateException;
import com.example.servicoderemessawallet.model.ExchangeRate;
import com.example.servicoderemessawallet.repository.ExchangeRateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateService {
    private static final String EXCHANGE_RATE_API_URL = "https://dadosabertos.bcb.gov.br/dataset/dolaramericano-usd-todos-os-boletins-diarios/resource/22ab054cb3ff-4864-82f7-b2815c7a77ec";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private BigDecimal lastValidExchangeRate = BigDecimal.valueOf(0.00); // Armazena a última cotação válida

    private final RestTemplate restTemplate;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(RestTemplate restTemplate,
                               ExchangeRateRepository exchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Scheduled(cron = "0 0 9 * * *") // Agendado para rodar todos os dias às 9h
    public void updateExchangeRate() {
        LocalDate today = LocalDate.now();

        if (isWeekend(today)) {
            System.out.println("É final de semana. Usando a última cotação disponível.");
            return;
        }

        try {
            updateExchangeRateForDate(today);
            System.out.println("Cotação atualizada para: " + lastValidExchangeRate);
        } catch (ExchangeRateException e) {
            System.out.println("Falha ao atualizar cotação: " + e.getMessage());
        }
    }

    public void updateDollarExchangeRate(LocalDate date) {
        if (isWeekend(date)) {
            System.out.println("É final de semana. Usando a última cotação disponível.");
            return;
        }

        // Utilizado para testes com datas específicas
        updateExchangeRateForDate(date);
    }

    private void updateExchangeRateForDate(LocalDate date) {
        String formattedDate = DATE_FORMATTER.format(date);
        URI uri = UriComponentsBuilder.fromUriString(EXCHANGE_RATE_API_URL)
                .queryParam("data", formattedDate)
                .build()
                .toUri();

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> response = responseEntity.getBody();
            if (response != null && response.containsKey("value")) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> value = objectMapper.convertValue(
                        response.get("value"),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                if (!value.isEmpty() && value.get(0).get("cotacaoCompra") != null) {
                    lastValidExchangeRate = new BigDecimal(value.get(0).get("cotacaoCompra").toString());
                    ExchangeRate exchangeRate = new ExchangeRate();
                    exchangeRate.setDate(date);
                    exchangeRate.setRate(lastValidExchangeRate);
                    exchangeRateRepository.save(exchangeRate);
                }
            }
        } catch (Exception e) {
            throw new ExchangeRateException("Não foi possível obter a cotação do dólar.");
        }
    }

    public BigDecimal getDollarExchangeRate() {
        return lastValidExchangeRate;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}