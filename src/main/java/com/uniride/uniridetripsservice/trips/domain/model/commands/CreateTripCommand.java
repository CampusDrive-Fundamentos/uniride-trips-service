package com.uniride.uniridetripsservice.trips.domain.model.commands;
import java.util.List;
public record CreateTripCommand(Long bookingId, Long routeId, String campus, String securityCode, List<Long> passengerIds) {}