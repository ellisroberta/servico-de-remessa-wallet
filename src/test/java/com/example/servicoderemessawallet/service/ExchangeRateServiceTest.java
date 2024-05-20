package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.exception.ExchangeRateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testUpdateDollarExchangeRate_Weekend() {
        // Simulando um final de semana (sábado)
        LocalDate weekendDate = LocalDate.of(2024, 5, 18); // Ajustar para um sábado válido

        exchangeRateService.updateDollarExchangeRate(weekendDate);

        BigDecimal expectedRate = BigDecimal.valueOf(0.00); // Deve manter a última cotação válida
        assertEquals(expectedRate, exchangeRateService.getDollarExchangeRate());
    }

    @Test
    public void testUpdateDollarExchangeRate_Weekday_Success() {
        // Simulando um dia útil (segunda a sexta)
        LocalDate weekdayDate = LocalDate.of(2024, 5, 20); // Ajustar para um dia útil válido

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("value", List.of(Map.of("cotacaoCompra", 5.25)));

        when(restTemplate.getForObject(any(), any())).thenReturn(mockResponse);

        exchangeRateService.updateDollarExchangeRate(weekdayDate);

        BigDecimal expectedRate = BigDecimal.valueOf(5.25); // Ajustar para a cotação esperada conforme o mock
        assertEquals(expectedRate, exchangeRateService.getDollarExchangeRate());
    }

    @Test
    public void testUpdateDollarExchangeRate_Weekday_EmptyResponse() {
        // Simulando um dia útil com resposta vazia do serviço externo
        LocalDate weekdayDate = LocalDate.of(2024, 5, 20); // Ajustar para um dia útil válido
        when(restTemplate.getForObject(any(), any())).thenReturn(null); // Simula resposta vazia

        exchangeRateService.updateDollarExchangeRate(weekdayDate);

        BigDecimal expectedRate = BigDecimal.valueOf(0.00); // Deve manter a última cotação válida
        assertEquals(expectedRate, exchangeRateService.getDollarExchangeRate());
    }

    @Test
    public void testUpdateDollarExchangeRate_Exception() {
        // Simulando uma exceção ao buscar a taxa de câmbio
        LocalDate weekdayDate = LocalDate.of(2024, 5, 20); // Ajustar para um dia útil válido
        when(restTemplate.getForObject(any(), any())).thenThrow(new RuntimeException("Erro no serviço externo"));

        assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.updateDollarExchangeRate(weekdayDate);
        });
    }

    private Map<String, Object> generateMockExchangeRateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("cotacaoCompra", BigDecimal.valueOf(5.25)); // Ajustar para a cotação desejada no mock
        return data;
    }
}
