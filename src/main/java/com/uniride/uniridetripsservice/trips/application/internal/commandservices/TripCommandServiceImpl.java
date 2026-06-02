package com.uniride.uniridetripsservice.trips.application.internal.commandservices;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.commands.CompleteTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.commands.CreateTripCommand;
import com.uniride.uniridetripsservice.trips.domain.model.commands.StartTripCommand;
import com.uniride.uniridetripsservice.trips.domain.services.TripCommandService;
import com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripCommandServiceImpl implements TripCommandService {

    private final TripRepository tripRepository;

    public TripCommandServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    @Transactional
    public Trip handle(CreateTripCommand command) {
        Trip trip = new Trip(command.bookingId(), command.driverId());
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(StartTripCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        trip.startTrip();
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip handle(CompleteTripCommand command) {
        Trip trip = tripRepository.findById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        trip.completeTrip();
        return tripRepository.save(trip);
    }
}