package com.uniride.uniridetripsservice.trips.infrastructure.outboundservices.finance;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Service
public class FinanceServiceIntegration {

    private final RestTemplate restTemplate;

    // Solución al warning: Se agregó "static" para que sea una constante global
    private static final String FINANCE_SERVICE_URL = "http://localhost:8083/api/v1/finance/settlements";

    public FinanceServiceIntegration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void reportTripCompletion(Long tripId, Long driverId, Double totalAmount) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = "";
            if (attributes != null) {
                token = attributes.getRequest().getHeader("Authorization");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", token);
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("tripId", tripId);
            payload.put("driverId", driverId);
            payload.put("totalAmount", totalAmount); // ¡Valor dinámico real!
            payload.put("paymentMethod", "CASH");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(FINANCE_SERVICE_URL, request, String.class);

            System.out.println(" ÉXITO: Cobro enviado a Finance. Respuesta: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println(" ERROR: No se pudo comunicar con Finance Service. " + e.getMessage());
        }
    }
}