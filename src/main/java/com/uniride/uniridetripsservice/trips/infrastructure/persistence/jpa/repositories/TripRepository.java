package com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByDriverIdAndStatus(Long driverId, TripStatus status);
}