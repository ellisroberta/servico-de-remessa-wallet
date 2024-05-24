package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.exception.ExchangeRateException;
import com.example.servicoderemessawallet.repository.ExchangeRateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "exchange.rate.api.url=https://dadosabertos.bcb.gov.br/dataset/dolaramericano-usd-todos-os-boletins-diarios/resource/22ab054cb3ff-4864-82f7-b2815c7a77ec"
})
public class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("Teste de atualização da taxa de câmbio em final de semana")
    public void testUpdateDollarExchangeRate_Weekend() {
        LocalDate weekendDate = LocalDate.of(2024, 5, 18); // Simula um sábado válido

        exchangeRateService.updateDollarExchangeRate(weekendDate);

        BigDecimal expectedRate = BigDecimal.valueOf(0.00); // Deve manter a última cotação válida
        assertEquals(expectedRate, exchangeRateService.getDollarExchangeRate());
    }

    @Test
    @DisplayName("Teste de atualização da taxa de câmbio em dia útil com sucesso")
    public void testUpdateDollarExchangeRate_Weekday_Success() {
        Map<String, Object> responseMap = new HashMap<>();
        List<Map<String, Object>> valueList = List.of(
                Map.of("cotacaoCompra", 5.20)
        );
        responseMap.put("value", valueList);
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseMap,
                HttpStatus.OK);

        when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        LocalDate testDate = LocalDate.now();

        exchangeRateService.updateExchangeRateForDate(testDate);

        BigDecimal expectedRate = BigDecimal.valueOf(5.20);
        BigDecimal actualRate = exchangeRateService.getDollarExchangeRate();
        assertEquals(expectedRate, actualRate, "Taxa de câmbio atualizada corretamente");
    }



    @Test
    @DisplayName("Teste de atualização da taxa de câmbio em dia útil com resposta vazia")
    public void testUpdateDollarExchangeRate_Weekday_EmptyResponse() {
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
                any(), eq(new ParameterizedTypeReference<Map<String, Object>>() {})))
                .thenReturn(responseEntity);

        LocalDate testDate = LocalDate.now();

        assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.updateExchangeRateForDate(testDate);
        }, "Deveria lançar uma ExchangeRateException para resposta vazia da API");
    }

    @Test
    @DisplayName("Teste de falha ao obter taxa de câmbio")
    public void testUpdateDollarExchangeRate_Exception() {
        LocalDate weekdayDate = LocalDate.of(2024, 5, 20); // Simula um dia útil com exceção

        when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Erro no serviço externo"));

        assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.updateDollarExchangeRate(weekdayDate);
        });
    }
}
