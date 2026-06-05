package com.uniride.uniridetripsservice.trips.infrastructure.outboundservices.finance;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${finance.service.url}")
    private String financeServiceUrl;

    public FinanceServiceIntegration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void reportTripCompletion(Long tripId, Long driverId, Double totalAmount, String paymentMethod) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = (attributes != null) ? attributes.getRequest().getHeader("Authorization") : "";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);

            Map<String, Object> payload = new HashMap<>();
            payload.put("tripId", tripId);
            payload.put("driverId", driverId);
            payload.put("totalAmount", totalAmount);
            payload.put("paymentMethod", paymentMethod);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(financeServiceUrl + "/settlements", request, String.class);
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo comunicar con Finance: " + e.getMessage());
        }
    }

    public boolean isDriverBlocked(Long driverId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(financeServiceUrl + "/drivers/" + driverId + "/account", Map.class);
            Map<String, Object> body = response.getBody();
            return "BLOCKED".equals(body.get("accountStatus"));
        } catch (Exception e) {
            return true;
        }
    }
}