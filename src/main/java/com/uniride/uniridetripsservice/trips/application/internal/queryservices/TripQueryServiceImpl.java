package com.uniride.uniridetripsservice.trips.application.internal.queryservices;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.queries.GetActiveTripByDriverIdQuery;
import com.uniride.uniridetripsservice.trips.domain.model.queries.GetTripByIdQuery;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import com.uniride.uniridetripsservice.trips.domain.services.TripQueryService;
import com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories.TripRepository;
import org.springframework.stereotype.Service;

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
    public Optional<Trip> handle(GetActiveTripByDriverIdQuery query) {
        return tripRepository.findByDriverIdAndStatus(query.driverId(), TripStatus.IN_PROGRESS);
    }
}