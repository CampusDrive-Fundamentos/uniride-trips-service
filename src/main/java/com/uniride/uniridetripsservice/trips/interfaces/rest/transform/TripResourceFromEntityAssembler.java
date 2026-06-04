package com.uniride.uniridetripsservice.trips.interfaces.rest.transform;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.interfaces.rest.resources.TripResource;

public class TripResourceFromEntityAssembler {
    public static TripResource toResourceFromEntity(Trip entity) {
        return new TripResource(
                entity.getId(),
                entity.getBookingId(),
                entity.getRouteId(),
                entity.getCampus(),
                entity.getDriverId(),
                entity.getStatus().name()
        );
    }
}