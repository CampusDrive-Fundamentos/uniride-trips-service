package com.uniride.uniridetripsservice.trips.domain.services;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.CompleteTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.commands.CreateTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.commands.StartTripCommand;

public interface TripCommandService {
    Trip handle(CreateTripCommand command);
    Trip handle(StartTripCommand command);
    Trip handle(CompleteTripCommand command);
}