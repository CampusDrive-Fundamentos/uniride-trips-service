package com.uniride.uniridetripsservice.trips.domain.model.commands;

public record CancelTripCommand(Long tripId, String reason) {
}