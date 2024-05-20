package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.exception.ExchangeRateException;
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
    private static final String EXCHANGE_RATE_API_URL = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoDolarDia(dataCotacao=@dataCotacao)?@dataCotacao='{data}'&$top=1&$format=json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private BigDecimal lastValidExchangeRate = BigDecimal.valueOf(0.00); // Armazena a última cotação válida

    private final RestTemplate restTemplate;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 3600000) // Executa a cada 1 hora
    public void updateDollarExchangeRate() {
        LocalDate date = LocalDate.now();

        // Se for final de semana, não faz a consulta
        if (isWeekend(date)) {
            return; // Retorna sem fazer nada
        }

        updateExchangeRateForDate(date);
    }

    public void updateDollarExchangeRate(LocalDate date) {
        // Utilizado para testes com datas específicas
        updateExchangeRateForDate(date);
    }

    private void updateExchangeRateForDate(LocalDate date) {
        String formattedDate = DATE_FORMATTER.format(date);
        String url = EXCHANGE_RATE_API_URL.replace("{data}", formattedDate);
        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();

        try {
            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
            if (response != null && response.containsKey("value")) {
                List<Map<String, Object>> value = (List<Map<String, Object>>) response.get("value");
                if (!value.isEmpty() && value.get(0).get("cotacaoCompra") != null) {
                    lastValidExchangeRate = new BigDecimal(value.get(0).get("cotacaoCompra").toString()); // Atualiza a última cotação válida
                }
            }
        } catch (Exception e) {
            throw new ExchangeRateException("Não foi possível obter a cotação do dólar.");
        }
    }

    public BigDecimal getDollarExchangeRate() {
        // Retorna a última cotação válida armazenada
        return lastValidExchangeRate;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}