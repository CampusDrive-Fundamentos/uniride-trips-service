package com.uniride.uniridetripsservice.trips.infrastructure.outboundservices.finance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    @Value("${finance.service.url}")
    private String financeServiceUrl;

    public FinanceServiceIntegration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String token = attributes.getRequest().getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", token);
            }
        }
        return headers;
    }

    public void reportTripCompletion(Long tripId, Long driverId, Double totalAmount, String paymentMethod) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("tripId", tripId);
            payload.put("driverId", driverId);
            payload.put("totalAmount", totalAmount);
            payload.put("paymentMethod", paymentMethod);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, getAuthHeaders());
            restTemplate.postForEntity(financeServiceUrl + "/settlements", request, String.class);
            System.out.println("ÉXITO: Cobro enviado a Finance.");
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo comunicar con Finance: " + e.getMessage());
        }
    }

    public boolean isDriverBlocked(Long driverId) {
        try {
            HttpEntity<String> request = new HttpEntity<>(getAuthHeaders());
            String targetUrl = financeServiceUrl + "/drivers/" + driverId + "/account";

            ResponseEntity<Map> response = restTemplate.exchange(targetUrl, HttpMethod.GET, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = (String) response.getBody().get("accountStatus");
                return "BLOCKED".equalsIgnoreCase(status);
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo verificar la deuda en Finance. " + e.getMessage());
        }
        return false;
    }
}