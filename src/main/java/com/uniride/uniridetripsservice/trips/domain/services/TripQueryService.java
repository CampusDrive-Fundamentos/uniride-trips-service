package com.uniride.uniridetripsservice.trips.domain.services;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface TripQueryService {
    Optional<Trip> handle(GetTripByIdQuery query);
    List<Trip> handle(GetAvailableTripsQuery query);
    Optional<Trip> handle(GetCurrentTripQuery query);
}