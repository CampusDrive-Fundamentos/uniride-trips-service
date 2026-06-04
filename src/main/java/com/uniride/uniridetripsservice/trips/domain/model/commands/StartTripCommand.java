package com.uniride.uniridetripsservice.trips.domain.model.commands;
public record StartTripCommand(Long tripId, String securityCode) {}