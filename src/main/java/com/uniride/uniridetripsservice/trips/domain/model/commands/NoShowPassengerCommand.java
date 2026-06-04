package com.uniride.uniridetripsservice.trips.domain.model.commands;
public record NoShowPassengerCommand(Long tripId, Long passengerId) {}