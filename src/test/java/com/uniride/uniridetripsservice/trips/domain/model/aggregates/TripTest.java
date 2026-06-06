package com.uniride.uniridetripsservice.trips.domain.model.aggregates;

import com.uniride.uniridetripsservice.trips.domain.model.valueobjects.TripStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TripTest {

    @Test
    void shouldStartTripWhenStatusIsPending() {
        Trip trip = new Trip(1L, 100L);

        trip.startTrip();

        assertEquals(TripStatus.IN_PROGRESS, trip.getStatus());
    }

    @Test
    void shouldCompleteTripWhenStatusIsInProgress() {
        Trip trip = new Trip(1L, 100L);
        trip.startTrip();

        trip.completeTrip();

        assertEquals(TripStatus.COMPLETED, trip.getStatus());
    }
}
