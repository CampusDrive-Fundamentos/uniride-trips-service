package com.uniride.uniridetripsservice.trips.infrastructure.persistence.jpa.repositories;

import com.uniride.uniridetripsservice.trips.domain.model.aggregates.Trip;
import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByCampusAndStatus(String campus, TripStatus status);

    @Query("SELECT t FROM Trip t LEFT JOIN t.passengers p WHERE (t.driverId = :userId OR p.passengerId = :userId) AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    Optional<Trip> findActiveTripByUserId(@Param("userId") Long userId);
}