package com.uniride.uniridetripsservice.trips.application.internal.queryservices;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.queries.*;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import com.uniride.uniridetripsservice.trips.domain.services.TripQueryService;
import com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripQueryServiceImpl implements TripQueryService {

    private final TripRepository tripRepository;

    public TripQueryServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Optional<Trip> handle(GetTripByIdQuery query) {
        return tripRepository.findById(query.tripId());
    }

    @Override
    public List<Trip> handle(GetAvailableTripsQuery query) {
        return tripRepository.findByCampusAndStatus(query.campus(), TripStatus.REQUESTED);
    }

    @Override
    public Optional<Trip> handle(GetCurrentTripQuery query) {
        return tripRepository.findActiveTripByUserId(query.userId());
    }
}