package com.uniride.uniridetripsservice.trips.domain.services;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.queries.GetActiveTripByDriverIdQuery;
import com.uniride.uniridetripsservice.trips.domain.model.queries.GetTripByIdQuery;

import java.util.Optional;

public interface TripQueryService {
    Optional<Trip> handle(GetTripByIdQuery query);
    Optional<Trip> handle(GetActiveTripByDriverIdQuery query);
}