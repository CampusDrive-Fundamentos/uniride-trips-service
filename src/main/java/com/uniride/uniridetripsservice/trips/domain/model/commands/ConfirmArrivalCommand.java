package com.uniride.uniridetripsservice.trips.domain.model.commands;
public record ConfirmArrivalCommand(Long tripId, Long passengerId) {}