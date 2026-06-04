package com.uniride.uniridetripsservice.trips.domain.services;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.*;

public interface TripCommandService {
    Trip handle(CreateTripCommand command);
    Trip handle(AcceptTripCommand command);
    Trip handle(StartTripCommand command);
    Trip handle(ConfirmArrivalCommand command);
    Trip handle(NoShowPassengerCommand command);
    Trip handle(CompleteTripCommand command);
    Trip handle(CancelTripCommand command);
}