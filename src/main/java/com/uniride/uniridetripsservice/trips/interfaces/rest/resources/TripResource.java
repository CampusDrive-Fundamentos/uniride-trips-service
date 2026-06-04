package com.uniride.uniridetripsservice.trips.interfaces.rest.resources;

public record TripResource(
        Long id,
        Long bookingId,
        Long routeId,
        String campus,
        Long driverId,
        String status
) {}