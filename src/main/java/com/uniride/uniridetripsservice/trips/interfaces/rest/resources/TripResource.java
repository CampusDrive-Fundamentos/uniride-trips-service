package com.uniride.uniridetripsservice.trips.interfaces.rest.resources;

public record TripResource(Long id, Long bookingId, Long driverId, String status) {}