package com.uniride.uniridetripsservice.trips.interfaces.rest.resources;
import java.util.List;
public record CreateTripRequest(Long bookingId, Long routeId, String campus, String securityCode, Double totalAmount, String paymentMethod, List<Long> passengerIds) {}