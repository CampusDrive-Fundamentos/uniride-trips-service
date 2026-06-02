package com.uniride.uniridetripsservice.trips.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record CreateTripRequest(
        @NotNull(message = "El ID de la reserva (Booking) es obligatorio") Long bookingId,
        @NotNull(message = "El ID del conductor es obligatorio") Long driverId
) {}