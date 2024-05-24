package com.example.servicoderemessawallet.service;

import com.example.servicoderemessawallet.exception.ExchangeRateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

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

//    @Test
//    @DisplayName("Teste de atualização da taxa de câmbio em dia útil com sucesso")
//    public void testUpdateDollarExchangeRate_Weekday_Success() {
//        LocalDate weekdayDate = LocalDate.of(2024, 5, 20); // Simula um dia útil válido
//
//        Map<String, Object> mockResponse = new HashMap<>();
//        mockResponse.put("value", List.of(Map.of("cotacaoCompra", 5.25))); // Ajustar para a cotação desejada no mock
//
//        ResponseEntity<Map<String, Object>> mockResponseEntity = ResponseEntity.ok(mockResponse);
//
//        // Utilize doReturn() para configurar o mock do RestTemplate
//        doReturn(mockResponseEntity)
//                .when(restTemplate)
//                .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class));
//
//        exchangeRateService.updateDollarExchangeRate(weekdayDate);
//
//        BigDecimal expectedRate = BigDecimal.valueOf(5.25); // Ajustar para a cotação esperada conforme o mock
//        assertEquals(expectedRate, exchangeRateService.getDollarExchangeRate());
//    }

    @Test
    @DisplayName("Teste de atualização da taxa de câmbio em dia útil com resposta vazia")
    public void testUpdateDollarExchangeRate_Weekday_EmptyResponse() {
        LocalDate weekdayDate = LocalDate.of(2024, 5, 20); // Simula um dia útil com resposta vazia

        when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok().build());

        exchangeRateService.updateDollarExchangeRate(weekdayDate);

        BigDecimal expectedRate = BigDecimal.valueOf(0.00); // Deve manter a última cotação válida
        assertEquals(expectedRate, exchangeRateService.getDollarExchangeRate());
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
