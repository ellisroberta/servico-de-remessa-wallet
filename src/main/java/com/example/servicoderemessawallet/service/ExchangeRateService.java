package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.exception.ExchangeRateException;
import com.example.servicoderemessawallet.model.ExchangeRate;
import com.example.servicoderemessawallet.repository.ExchangeRateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private static final String EXCHANGE_RATE_API_URL = "https://dadosabertos.bcb.gov.br/dataset/dolaramericano-usd-todos-os-boletins-diarios/resource/22ab054cb3ff-4864-82f7-b2815c7a77ec";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private BigDecimal lastValidExchangeRate = BigDecimal.valueOf(0.00);

    private final RestTemplate restTemplate;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(RestTemplate restTemplate,
                               ExchangeRateRepository exchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Scheduled(cron = "0 0 9 * * MON-FRI") // Agendado para rodar de segunda a sexta-feira às 9h
    public void updateExchangeRate() {
        LocalDate today = LocalDate.now();
        try {
            updateExchangeRateForDate(today);
            logger.info("Cotação atualizada para: {}", lastValidExchangeRate);
        } catch (ExchangeRateException e) {
            logger.error("Falha ao atualizar cotação: {}", e.getMessage());
        }
    }

    public void updateDollarExchangeRate(LocalDate date) {
        updateExchangeRateForDate(date);
    }

    public void updateExchangeRateForDate(LocalDate date) {
        try {
            String formattedDate = DATE_FORMATTER.format(date);
            URI uri = UriComponentsBuilder.fromUriString(EXCHANGE_RATE_API_URL)
                    .queryParam("data", formattedDate)
                    .build()
                    .toUri();

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (!responseEntity.hasBody()) {
                throw new ExchangeRateException("Resposta inválida ou vazia da API.");
            }

            Map<String, Object> response = responseEntity.getBody();
            if (response == null || !response.containsKey("value")) {
                throw new ExchangeRateException("Chave 'value' ausente na resposta da API.");
            }

            List<Map<String, Object>> value = (List<Map<String, Object>>) response.get("value");
            if (value.isEmpty()) {
                throw new ExchangeRateException("Lista de valores 'value' está vazia na resposta da API.");
            }

            Object cotacaoCompra = value.get(0).get("cotacaoCompra");
            if (!(cotacaoCompra instanceof Number)) {
                throw new ExchangeRateException("Valor de 'cotacaoCompra' inválido na resposta da API.");
            }

            lastValidExchangeRate = new BigDecimal(cotacaoCompra.toString());

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setDate(date);
            exchangeRate.setRate(lastValidExchangeRate);
            exchangeRateRepository.save(exchangeRate);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ExchangeRateException("Erro ao acessar a API do Banco Central: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExchangeRateException("Não foi possível obter a cotação do dólar.", e);
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